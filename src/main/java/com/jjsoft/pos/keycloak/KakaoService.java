package com.jjsoft.pos.keycloak;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoService {
	

	private final RestTemplate restTemplate = new RestTemplate();
	

    /**
     * 카카오 사용자 정보 가져오기
     * @param accessToken 카카오 access_token
     * @return 카카오 유저 정보 JSON
     */
    public KakaoUserInfoDto getKakaoUserInfo(String accessToken , String userId) {
    	try {
    		// 카카오 사용자 정보 API URL
            String url = "https://kapi.kakao.com/v2/user/me";

            // 요청 헤더 세팅
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + accessToken);

            HttpEntity<Void> request = new HttpEntity<>(headers);

            // API 호출
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    request,
                    String.class
            );
            String kakaoUserInfo = response.getBody();
            System.out.println("kakaoUserInfo == " + kakaoUserInfo);
            
            ObjectMapper mapper = new ObjectMapper();
            KakaoUserInfoDto info = mapper.readValue(kakaoUserInfo, KakaoUserInfoDto.class);
            
            String address = getKakaoUserAddress(accessToken);
            info.setAddress(address);
            System.out.println("kakao response info == " + info.toString());
            
            return info;
		} catch (Exception e) {
			// TODO: handle exception
			return null;
		}
        
    }
    
    public String getKakaoUserAddress(String accessToken) {
    	try {
    		// 카카오 사용자 정보 API URL
			String url = "https://kapi.kakao.com/v1/user/shipping_address";
			
			HttpHeaders headers = new HttpHeaders();
			headers.set("Authorization", "Bearer " + accessToken);
			
			HttpEntity<Void> request = new HttpEntity<>(headers);
			
			ResponseEntity<KakaoShippingAddressResponse> response = restTemplate.exchange(
			        url,
			        HttpMethod.GET,
			        request,
			        KakaoShippingAddressResponse.class
			);
			KakaoShippingAddressResponse info = response.getBody();
//			System.out.println("getKakaoUserAddress response === "+response);
//			System.out.println("getKakaoUserAddress response.getBody() === "+info);
			if(info.getShippingAddresses() != null && info.getShippingAddresses().size() > 0) {
				KakaoShippingAddressResponse.ShippingAddress     ss = info.getShippingAddresses().stream().filter(x->x.getIsDefault()).findAny().orElse(null);
				KakaoShippingAddressResponse.ShippingAddress target = (ss != null ? ss : info.getShippingAddresses().get(0));
				String address = target.getBaseAddress() != null ? target.getBaseAddress() : "";
			    return address;
			}
			return "";
			
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("getKakaoUserAddress ERROR === ");
//			e.printStackTrace();
			return null;
		}
        
    }
    
    
  //카카오 동의 철회
    public long unlinkWithAccessToken(String userAccessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + userAccessToken);
        // ✅ 누락된 헤더: 폼-urlencoded + UTF-8
        headers.setContentType(MediaType.valueOf("application/x-www-form-urlencoded;charset=utf-8"));

        // ✅ 본문은 빈 폼데이터여야 함 (JSON/빈 바디 X)
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(form, headers);

        try {
            ResponseEntity<KakaoUnlinkResponse> resp = restTemplate.exchange(
                    "https://kapi.kakao.com/v1/user/unlink",
                    HttpMethod.POST,
                    entity,
                    KakaoUnlinkResponse.class
            );
            KakaoUnlinkResponse body = resp.getBody();
            if (resp.getStatusCode().is2xxSuccessful() && body != null && body.getId() != null) {
                return body.getId();
            }
            log.error("############# 카카오 동의철회 에러 body 없음 ############  token = {}", userAccessToken);
            return -1L;
        } catch (HttpStatusCodeException e) {
            log.error("############# 카카오 동의철회 에러 ############  token = {}", userAccessToken);
            e.printStackTrace();
            return -1L;
        }
    }
    
    @Data
    public static class KakaoUnlinkResponse {
        private Long id;
    }
	
}
