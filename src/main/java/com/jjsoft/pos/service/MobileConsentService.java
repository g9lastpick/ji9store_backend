package com.jjsoft.pos.service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jjsoft.pos.entity.UserMstEntity;
import com.jjsoft.pos.keycloak.KakaoService;
import com.jjsoft.pos.keycloak.KakaoTokenResponse;
import com.jjsoft.pos.keycloak.KakaoUserInfoDto;
import com.jjsoft.pos.repository.KeycloakFederatedIdentityRepository;
import com.jjsoft.pos.repository.UserMstRepository;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 * 마이페이지 선택동의(카카오 선택 프로필) 동의/철회 + 카카오싱크 연동.
 *
 * 대상 항목 (Kakao scope ↔ user_mst 컬럼) — 가입 선택동의 4항목과 정렬
 *   - 성별     gender           ↔ GENDER
 *   - 생일     birthday         ↔ BIRTHDAY
 *   - 출생년도 birthyear        ↔ BIRTHYEAR
 *   - 주소     shipping_address ↔ ADDRESS
 *
 * 철회: 카카오 revoke/scopes 호출 성공 시 user_mst 해당 컬럼을 NULL 처리(데이터 삭제).
 * 동의: 서버만으로 불가 → 카카오 추가동의 OAuth(authorize)로 리다이렉트한 뒤,
 *       콜백의 code 를 토큰으로 교환하여 /v2/user/me 로 데이터를 재수집해 user_mst 에 적재한다.
 *
 * 카카오 access token 은 Keycloak FEDERATED_IDENTITY.TOKEN(JSON)에 저장되어 있으며
 * 로그인 시점 기준이라 만료될 수 있다 → 401 시 refresh_token 으로 1회 갱신 후 재시도하고
 * 갱신 토큰을 다시 저장한다.
 */
@Service
@RequiredArgsConstructor
@Log4j2
public class MobileConsentService {

    private final KeycloakFederatedIdentityRepository federatedRepo;
    private final UserMstRepository userMstRepository;
    private final KakaoService kakaoService;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper mapper = new ObjectMapper();

    @Value("${kakao.rest-key}")
    private String kakaoRestKey;
    @Value("${kakao.client-secret}")
    private String kakaoClientSecret;
    @Value("${kakao.consent-redirect-uri}")
    private String consentRedirectUri;

    /** 관리 대상 선택동의 항목 (가입 선택동의 4항목 순서: 성별·생일·출생년도·주소) */
    private static final String[] MANAGED_SCOPES = {"gender", "birthday", "birthyear", "shipping_address"};
    private static final Map<String, String> SCOPE_LABEL = Map.of(
            "gender", "성별",
            "birthday", "생일",
            "birthyear", "출생년도",
            "shipping_address", "주소");

    /* ===================== 조회 ===================== */

    public List<ConsentStatusDto> getStatus(String userId) {
        String accessToken = currentAccessToken(userId);
        Map<String, ScopeMeta> scopes = callScopes(accessToken);
        if (scopes == null) {
            // 토큰 만료 가능성 → refresh 후 재시도
            String refreshed = refreshAndStore(userId);
            if (refreshed != null) {
                scopes = callScopes(refreshed);
            }
        }
        if (scopes == null) {
            scopes = Map.of();
        }

        UserMstEntity u = userMstRepository.getUserByUserId(userId).orElse(null);
        List<ConsentStatusDto> result = new ArrayList<>();
        for (String scope : MANAGED_SCOPES) {
            ScopeMeta meta = scopes.get(scope);
            ConsentStatusDto dto = new ConsentStatusDto();
            dto.setScope(scope);
            dto.setLabel(SCOPE_LABEL.get(scope));
            dto.setAvailable(meta != null);                       // 카카오 앱에 동의항목으로 설정되어 있는지
            dto.setAgreed(meta != null && meta.agreed);           // 현재 동의 여부
            dto.setRevocable(meta != null && meta.revocable);     // 철회 가능 여부(필수동의면 false)
            dto.setHasData(hasData(u, scope));
            result.add(dto);
        }
        return result;
    }

    /* ===================== 철회 ===================== */

    @Transactional
    public boolean revoke(String userId, List<String> scopes) {
        List<String> targets = sanitize(scopes);
        if (targets.isEmpty()) {
            return false;
        }

        String accessToken = currentAccessToken(userId);
        boolean ok = callRevoke(accessToken, targets);
        if (!ok) {
            String refreshed = refreshAndStore(userId);
            if (refreshed != null) {
                ok = callRevoke(refreshed, targets);
            }
        }
        if (!ok) {
            log.error("카카오 동의철회 실패 userId={} scopes={}", userId, targets);
            return false;
        }

        // 카카오 철회 성공 → 우리 DB 의 해당 데이터 삭제
        UserMstEntity u = userMstRepository.getUserByUserId(userId).orElse(null);
        if (u != null) {
            for (String scope : targets) {
                clearColumn(u, scope);
            }
            userMstRepository.save(u);
        }
        return true;
    }

    /* ===================== 추가 동의 (재동의) ===================== */

    /** 카카오 추가동의 authorize URL 생성. state 에 scope 를 실어 콜백에서 식별. */
    public String buildReconsentUrl(String scope) {
        if (scope == null || !SCOPE_LABEL.containsKey(scope)) {
            return null;
        }
        return "https://kauth.kakao.com/oauth/authorize"
                + "?response_type=code"
                + "&client_id=" + enc(kakaoRestKey)
                + "&redirect_uri=" + enc(consentRedirectUri)
                + "&scope=" + enc(scope)
                + "&state=" + enc(scope);
    }

    /**
     * 추가동의 콜백 처리: code→token 교환 → /v2/user/me 로 데이터 재수집 → user_mst 적재.
     * 갱신된 카카오 토큰은 FEDERATED_IDENTITY 에도 반영해 서버측 호출이 계속 유효하도록 한다.
     */
    @Transactional
    public boolean reconsentCallback(String userId, String code, String scope) {
        if (code == null || scope == null || !SCOPE_LABEL.containsKey(scope)) {
            return false;
        }
        KakaoTokenResponse tok = exchangeCodeForToken(code);
        if (tok == null || tok.getAccessToken() == null) {
            log.error("추가동의 code 교환 실패 userId={} scope={}", userId, scope);
            return false;
        }

        // 새 토큰을 federated 저장소에 반영(다음 서버측 호출이 유효하도록)
        storeRefreshedToken(userId, tok);

        // 새 access token 으로 사용자 정보 재수집 (KakaoService 가 /v2/user/me + 배송지 모두 조회)
        KakaoUserInfoDto info = kakaoService.getKakaoUserInfo(tok.getAccessToken(), userId);
        if (info == null) {
            log.error("추가동의 후 카카오 사용자정보 조회 실패 userId={} scope={}", userId, scope);
            return false;
        }

        UserMstEntity u = userMstRepository.getUserByUserId(userId).orElse(null);
        if (u == null) {
            return false;
        }
        applyColumn(u, scope, info);
        userMstRepository.save(u);
        return true;
    }

    /* ===================== 내부 헬퍼 - 카카오 HTTP ===================== */

    /** GET /v2/user/scopes → {scopeId: ScopeMeta}. 실패(만료 등) 시 null. */
    private Map<String, ScopeMeta> callScopes(String accessToken) {
        if (accessToken == null) {
            return null;
        }
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
            HttpEntity<Void> req = new HttpEntity<>(headers);
            ResponseEntity<String> resp = restTemplate.exchange(
                    "https://kapi.kakao.com/v2/user/scopes", HttpMethod.GET, req, String.class);
            JsonNode root = mapper.readTree(resp.getBody());
            Map<String, ScopeMeta> out = new LinkedHashMap<>();
            for (JsonNode s : root.path("scopes")) {
                ScopeMeta meta = new ScopeMeta();
                meta.agreed = s.path("agreed").asBoolean(false);
                // revocable 필드는 동의된 항목에만 존재. 미동의 항목은 동의 가능(ON)하므로 의미 없음.
                meta.revocable = s.path("revocable").asBoolean(false);
                out.put(s.path("id").asText(), meta);
            }
            return out;
        } catch (Exception e) {
            log.warn("callScopes 실패(토큰 만료 가능): {}", e.getMessage());
            return null;
        }
    }

    private static class ScopeMeta {
        boolean agreed;
        boolean revocable;
    }

    /** POST /v2/user/revoke/scopes (scopes 는 폼 바디로 전달: scopes=["a","b"]) */
    private boolean callRevoke(String accessToken, List<String> scopes) {
        if (accessToken == null) {
            return false;
        }
        try {
            String scopesJson = mapper.writeValueAsString(scopes); // ["shipping_address"]
            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
            headers.setContentType(MediaType.valueOf("application/x-www-form-urlencoded;charset=utf-8"));
            MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
            form.add("scopes", scopesJson);
            HttpEntity<MultiValueMap<String, String>> req = new HttpEntity<>(form, headers);
            ResponseEntity<String> resp = restTemplate.exchange(
                    "https://kapi.kakao.com/v2/user/revoke/scopes", HttpMethod.POST, req, String.class);
            return resp.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            log.warn("callRevoke 실패: {}", e.getMessage());
            return false;
        }
    }

    /** POST kauth/oauth/token grant_type=authorization_code */
    private KakaoTokenResponse exchangeCodeForToken(String code) {
        try {
            MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
            form.add("grant_type", "authorization_code");
            form.add("client_id", kakaoRestKey);
            form.add("client_secret", kakaoClientSecret);
            form.add("redirect_uri", consentRedirectUri);
            form.add("code", code);
            return postToken(form);
        } catch (Exception e) {
            log.error("exchangeCodeForToken 실패", e);
            return null;
        }
    }

    /** POST kauth/oauth/token grant_type=refresh_token */
    private KakaoTokenResponse refreshAccessToken(String refreshToken) {
        if (refreshToken == null) {
            return null;
        }
        try {
            MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
            form.add("grant_type", "refresh_token");
            form.add("client_id", kakaoRestKey);
            form.add("client_secret", kakaoClientSecret);
            form.add("refresh_token", refreshToken);
            return postToken(form);
        } catch (Exception e) {
            log.error("refreshAccessToken 실패", e);
            return null;
        }
    }

    private KakaoTokenResponse postToken(MultiValueMap<String, String> form) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf("application/x-www-form-urlencoded;charset=utf-8"));
        HttpEntity<MultiValueMap<String, String>> req = new HttpEntity<>(form, headers);
        ResponseEntity<KakaoTokenResponse> resp = restTemplate.exchange(
                "https://kauth.kakao.com/oauth/token", HttpMethod.POST, req, KakaoTokenResponse.class);
        return resp.getBody();
    }

    /* ===================== 내부 헬퍼 - federated 토큰 ===================== */

    private String currentAccessToken(String userId) {
        try {
            String json = federatedRepo.findFederatedToken(userId);
            if (json == null) {
                return null;
            }
            return mapper.readTree(json).path("access_token").asText(null);
        } catch (Exception e) {
            log.warn("currentAccessToken 파싱 실패: {}", e.getMessage());
            return null;
        }
    }

    /** refresh_token 으로 access token 갱신 후 federated 저장소 반영. 새 access token 반환(실패 null). */
    private String refreshAndStore(String userId) {
        try {
            String json = federatedRepo.findFederatedToken(userId);
            if (json == null) {
                return null;
            }
            JsonNode root = mapper.readTree(json);
            String refreshToken = root.path("refresh_token").asText(null);
            KakaoTokenResponse refreshed = refreshAccessToken(refreshToken);
            if (refreshed == null || refreshed.getAccessToken() == null) {
                return null;
            }
            mergeAndSave(userId, json, refreshed);
            return refreshed.getAccessToken();
        } catch (Exception e) {
            log.error("refreshAndStore 실패", e);
            return null;
        }
    }

    private void storeRefreshedToken(String userId, KakaoTokenResponse tok) {
        try {
            String json = federatedRepo.findFederatedToken(userId);
            if (json == null) {
                return; // federated 행이 없으면 갱신만 생략(데이터 적재는 그대로 진행)
            }
            mergeAndSave(userId, json, tok);
        } catch (Exception e) {
            log.warn("storeRefreshedToken 실패: {}", e.getMessage());
        }
    }

    /** Keycloak 이 저장한 원본 JSON 구조를 유지한 채 토큰 필드만 갱신해 저장. */
    private void mergeAndSave(String userId, String originalJson, KakaoTokenResponse tok) throws Exception {
        ObjectNode obj = (ObjectNode) mapper.readTree(originalJson);
        obj.put("access_token", tok.getAccessToken());
        if (tok.getRefreshToken() != null) {
            obj.put("refresh_token", tok.getRefreshToken());
        }
        if (tok.getExpiresIn() != null) {
            obj.put("expires_in", tok.getExpiresIn());
        }
        federatedRepo.updateFederatedToken(userId, mapper.writeValueAsString(obj));
    }

    /* ===================== 내부 헬퍼 - 컬럼 매핑 ===================== */

    private boolean hasData(UserMstEntity u, String scope) {
        if (u == null) {
            return false;
        }
        switch (scope) {
            case "shipping_address": return notBlank(u.getAddress());
            case "birthday":         return notBlank(u.getBirthday());
            case "gender":           return notBlank(u.getGender());
            case "birthyear":        return notBlank(u.getBirthYear());
            default:                 return false;
        }
    }

    private void clearColumn(UserMstEntity u, String scope) {
        switch (scope) {
            case "shipping_address": u.setAddress(null);   break;
            case "birthday":         u.setBirthday(null);  break;
            case "gender":           u.setGender(null);    break;
            case "birthyear":        u.setBirthYear(null); break;
            default: break;
        }
    }

    private void applyColumn(UserMstEntity u, String scope, KakaoUserInfoDto info) {
        KakaoUserInfoDto.KakaoAccount acc = info.getKakao_account();
        switch (scope) {
            case "shipping_address":
                if (notBlank(info.getAddress())) u.setAddress(info.getAddress());
                break;
            case "birthday":
                if (acc != null && notBlank(acc.getBirthday())) u.setBirthday(acc.getBirthday());
                break;
            case "gender":
                if (acc != null && notBlank(acc.getGender())) u.setGender(acc.getGender());
                break;
            case "birthyear":
                if (acc != null && notBlank(acc.getBirthyear())) u.setBirthYear(acc.getBirthyear());
                break;
            default: break;
        }
    }

    private List<String> sanitize(List<String> scopes) {
        List<String> out = new ArrayList<>();
        if (scopes != null) {
            for (String s : scopes) {
                if (s != null && SCOPE_LABEL.containsKey(s) && !out.contains(s)) {
                    out.add(s);
                }
            }
        }
        return out;
    }

    private static boolean notBlank(String s) {
        return s != null && !s.trim().isEmpty();
    }

    private static String enc(String v) {
        return URLEncoder.encode(v, StandardCharsets.UTF_8);
    }

    /* ===================== DTO ===================== */

    @Data
    public static class ConsentStatusDto {
        private String scope;     // 카카오 scope id
        private String label;     // 화면 표시명
        private boolean available; // 카카오 앱에 동의항목으로 설정되어 있는지
        private boolean agreed;    // 카카오 동의 여부
        private boolean revocable; // 철회 가능 여부(필수동의면 false)
        private boolean hasData;   // user_mst 보유 여부
    }
}
