package com.jjsoft.pos.repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository
public class KeycloakFederatedIdentityRepository {

	// ✅ Keycloak DB 연결 정보 (직접 관리)
	@Value("${app.keycloak-db-url}")
	private String keycloakUrl;
	@Value("${app.keycloak-db-user}")
	private String keycloakUser;
	@Value("${app.keycloak-db-pwd}")
	private String keycloakPwd;

	// 보안: 하드코딩된 DB 접속정보/비밀번호 제거. 접속정보는 @Value 외부 주입(keycloakUrl/keycloakUser/keycloakPwd) 사용


//    public String findFederatedToken(String userId) {
//        String sql = "SELECT TOKEN FROM FEDERATED_IDENTITY WHERE FEDERATED_USERNAME = ? AND IDENTITY_PROVIDER = 'oidc-kakao'";
//        try (Connection con = dataSource.getConnection();
//             PreparedStatement ps = con.prepareStatement(sql)) {
//            ps.setString(1, userId);
//            try (ResultSet rs = ps.executeQuery()) {
//                return rs.next() ? rs.getString("TOKEN") : null;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
    
    public String findFederatedToken(String userId) {
        String sql = "SELECT TOKEN " +
                     "FROM FEDERATED_IDENTITY " +
                     "WHERE FEDERATED_USERNAME = ? " +
                     "  AND IDENTITY_PROVIDER = 'oidc-kakao'";

        try (Connection con = DriverManager.getConnection(keycloakUrl, keycloakUser, keycloakPwd);
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("TOKEN");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 갱신(refresh)된 카카오 토큰 JSON 을 FEDERATED_IDENTITY.TOKEN 에 반영한다.
     * (Keycloak 이 저장한 원본 구조를 유지한 채 access_token 등만 교체해 넘겨받는다.)
     * @return 갱신 행 수(0 이면 미반영)
     */
    public int updateFederatedToken(String userId, String tokenJson) {
        String sql = "UPDATE FEDERATED_IDENTITY " +
                     "SET TOKEN = ? " +
                     "WHERE FEDERATED_USERNAME = ? " +
                     "  AND IDENTITY_PROVIDER = 'oidc-kakao'";

        try (Connection con = DriverManager.getConnection(keycloakUrl, keycloakUser, keycloakPwd);
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, tokenJson);
            ps.setString(2, userId);
            return ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * Keycloak sub(USER_ENTITY.ID) 묶음으로 USERNAME(preferred_username)을 일괄 조회한다.
     * 어드민 리뷰 목록에서 리뷰의 userId(sub)를 user_mst.userId(preferred_username)로 잇는 브리지.
     * @return sub -> username 맵 (조회 실패/없음은 키 누락)
     */
    public Map<String, String> findUsernamesBySub(List<String> subs) {
        if (subs == null || subs.isEmpty()) return Collections.emptyMap();

        String placeholders = subs.stream().map(s -> "?").collect(Collectors.joining(","));
        String sql = "SELECT ID, USERNAME FROM USER_ENTITY WHERE ID IN (" + placeholders + ")";

        Map<String, String> result = new HashMap<>();
        try (Connection con = DriverManager.getConnection(keycloakUrl, keycloakUser, keycloakPwd);
             PreparedStatement ps = con.prepareStatement(sql)) {

            for (int i = 0; i < subs.size(); i++) {
                ps.setString(i + 1, subs.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.put(rs.getString("ID"), rs.getString("USERNAME"));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /** reverse bridge: usernames -> keycloak sub list (admin phone search) */
    public List<String> findSubsByUsernames(List<String> usernames) {
        if (usernames == null || usernames.isEmpty()) return Collections.emptyList();
        String placeholders = usernames.stream().map(s -> "?").collect(Collectors.joining(","));
        String sql = "SELECT ID FROM USER_ENTITY WHERE USERNAME IN (" + placeholders + ")";
        List<String> result = new ArrayList<>();
        try (Connection con = DriverManager.getConnection(keycloakUrl, keycloakUser, keycloakPwd);
             PreparedStatement ps = con.prepareStatement(sql)) {
            for (int i = 0; i < usernames.size(); i++) ps.setString(i + 1, usernames.get(i));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) result.add(rs.getString("ID"));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return result;
    }
}
