package com.jjsoft.pos.keycloak;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;

import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

/**
 * 🔐 Keycloak Admin API와 통신하기 위한 access_token 제공 서비스
 * - client_credentials 방식으로 admin 토큰 발급
 * - access_token이 유효시간 지나면 자동으로 재발급
 */
@Service
@RequiredArgsConstructor
public class KeycloakTokenService {

    @Value("${keycloak.auth-server-url}")
    private String authServerUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.client-id}")
    private String clientId;

    @Value("${keycloak.credentials.secret}")
    private String clientSecret;
    
    @Value("${keycloak.admin-username}")
	private String adminUsername;
	
	@Value("${keycloak.admin-password}")
	private String adminPassword;
	
	@Value("${app.base-url}")
    private String baseUrl;

    private Keycloak keycloak;
    private Instant lastTokenTime;

    @PostConstruct
    public void init() {
        this.keycloak = createKeycloakInstance();
        this.lastTokenTime = Instant.now();
    }

    /**
     * ✅ 외부에서 주입 받을 Keycloak 인스턴스
     * - 내부적으로 access_token 유효시간이 지나면 갱신 처리
     */
    public Keycloak getKeycloak() {
        if (Duration.between(lastTokenTime, Instant.now()).toMinutes() >= 4) { // 보통 5분 만료 대비
            this.keycloak = createKeycloakInstance();
            this.lastTokenTime = Instant.now();
        }
        return this.keycloak;
    }

    /**
     * ⛏ 실제 Keycloak 인스턴스 생성 (토큰 요청 포함)
     */
    private Keycloak createKeycloakInstance() {
        return KeycloakBuilder.builder()
                .serverUrl(authServerUrl)
                .realm(realm)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .build();
    }
    
//    public String getAdminToken() {
//    	WebClient webClient = WebClient.builder().build();
//        Map<String, Object> response = webClient.post()
//                .uri(authServerUrl + "/realms/master/protocol/openid-connect/token")
//                .bodyValue(Map.of(
//                        "client_id", "admin-cli",
//                        "username", adminUsername,
//                        "password", adminPassword,
//                        "grant_type", "password"
//                ))
//                .retrieve()
//                .bodyToMono(Map.class)
//                .block();
//
//        return (String) response.get("access_token");
//    }
    
    public String getAdminToken() {
        WebClient webClient = WebClient.builder().build();

        Map<String, Object> response = webClient.post()
                .uri(baseUrl +"/realms/master/protocol/openid-connect/token")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .body(BodyInserters.fromFormData("client_id", "admin-cli")
                                   .with("username", "admin")
                                   .with("password", "admin")  // 형님 로컬 admin 비번
                                   .with("grant_type", "password"))
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        return response != null ? (String) response.get("access_token") : null;
    }
} 
