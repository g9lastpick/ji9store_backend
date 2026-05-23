package com.jjsoft.pos.repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

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
	
	
//    private static final String URL      = "jdbc:mariadb://<DB_HOST>:3306/keycloakdb";
//    private static final String USERNAME = "keycloak";
//    private static final String PASSWORD = "(removed)";


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
}
