package com.jjsoft.pos.keycloak;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jjsoft.pos.repository.KeycloakFederatedIdentityRepository;

import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
@Slf4j
@Service
@RequiredArgsConstructor
public class KeycloakService {

	
	
	private final KeycloakTokenService tokenService;
	private final KeycloakFederatedIdentityRepository keycloakFederatedIdentityRepository;
	private final KakaoService kakaoService;

	@Value("${keycloak.auth-server-url}")
    private String authServerUrl;
	@Value("${keycloak.realm}")
    private  String realm ;
	@Value("${keycloak.idp-alias}")
    private String kakaoIdpAlias;
	
	@Value("${app.base-url}")
    private String baseUrl;

	@Value("${app.mode}")
	private String mode;
	public KakaoUserInfoDto kakaoToken(String userId) {
		try {
			
			//로컬에서는 카카오 안타게
			if(mode != null && mode.equals("local")) {
				return null;
			}
			
			
			String kakaoTokenJson = keycloakFederatedIdentityRepository.findFederatedToken(userId);
			System.out.println("kakaoToken start ################################################################ " );
			System.out.println("kakaoToken value ################################################################ " +kakaoTokenJson);
			System.out.println("kakaoToken end   ################################################################ " );
			ObjectMapper mapper = new ObjectMapper();
	        KakaoTokenResponse token = mapper.readValue(kakaoTokenJson, KakaoTokenResponse.class);
			return kakaoService.getKakaoUserInfo(token.getAccessToken() , userId);
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("kakaoToken ERROR   ################################################################ " );
			return null;
		}
		
	}
	
	

    
    /** keycloak user 저장  */
//    public void createUser(String userId, String email, String password) {
//        UserRepresentation user = new UserRepresentation();
//        user.setUsername(userId);
//        user.setEmail(email);
//        user.setEnabled(true);
//
//        CredentialRepresentation credential = new CredentialRepresentation();
//        credential.setType(CredentialRepresentation.PASSWORD);
//        credential.setValue(password);
//        credential.setTemporary(false);
//
//        user.setCredentials(List.of(credential));
//        Keycloak keycloak = tokenService.getKeycloak();
//        Response response = keycloak.realm(realm)
//                .users()
//                .create(user);
//
//        if (response.getStatus() != 201) {
//            throw new RuntimeException("Keycloak 사용자 생성 실패: " + response.getStatus());
//        }
//    }
//    
//    /** keycloak user 조회*/
//    public List<UserRepresentation> findUsersByUsername(String keyword) {
//    	Keycloak keycloak = tokenService.getKeycloak();
//        return keycloak.realm("jjpos")
//                .users()
//                .search(keyword);  // username, email 등으로 LIKE 검색
//    }
    
    
    public List<UserRepresentation> getAllUsers() {
        return tokenService.getKeycloak().realm(realm).users().list();
    }

    public List<UserRepresentation> searchUsers(String username, String email) {
        if (username != null) {
            return tokenService.getKeycloak().realm(realm).users().search(username);
        } else if (email != null) {
            return tokenService.getKeycloak().realm(realm).users().search(null, null, null, email, 0, 10);
        }
        return new ArrayList<>();
    }

    /** keycloak user 생성 기본 권한 : ROLE_USER */
    public void saveOrUpdateUser(KeycloakUserDto dto) {

        if (dto.getId() != null && !dto.getId().isEmpty()) {
        	// ID 기반 수정
            UserResource userResource = tokenService.getKeycloak().realm(realm).users().get(dto.getId());
            UserRepresentation user = userResource.toRepresentation();
            user.setEmail(dto.getEmail());
            user.setEnabled(dto.getEnabled());
            userResource.update(user);
        } else {
            UserRepresentation user = new UserRepresentation();
            user.setUsername(dto.getUsername());
            user.setEmail(dto.getEmail());
            user.setEnabled(Optional.ofNullable(dto.getEnabled()).orElse(true));
            user.setEmailVerified(true);

            var response = tokenService.getKeycloak().realm(realm).users().create(user);

            if (response.getStatus() == 201 && dto.getPassword() != null) {
                String userId = response.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");
                CredentialRepresentation password = new CredentialRepresentation();
                password.setType(CredentialRepresentation.PASSWORD);
                password.setValue(dto.getPassword());
                password.setTemporary(false);
                tokenService.getKeycloak().realm(realm).users().get(userId).resetPassword(password);
                
             // 🔹 기본 Role 확인 (ROLE_USER 없으면 생성)
                RolesResource rolesResource = tokenService.getKeycloak().realm(realm).roles();
                RoleRepresentation roleUser;
                try {
                    roleUser = rolesResource.get("USER").toRepresentation();
                } catch (NotFoundException e) {
                    // ROLE_USER 가 없으면 새로 생성
                    RoleRepresentation newRole = new RoleRepresentation();
                    newRole.setName("USER");
                    newRole.setDescription("Default role for new users");
                    rolesResource.create(newRole);

                    // 다시 가져오기
                    roleUser = rolesResource.get("USER").toRepresentation();
                }

                // 🔹 생성된 유저에 ROLE_USER 매핑
                tokenService.getKeycloak()
                    .realm(realm)
                    .users()
                    .get(userId)
                    .roles()
                    .realmLevel()
                    .add(Collections.singletonList(roleUser));
            }
        }
    }

    public void deleteUser(String username) {
        List<UserRepresentation> users = tokenService.getKeycloak().realm(realm).users().search(username);
        if (!users.isEmpty()) {
            tokenService.getKeycloak().realm(realm).users().get(users.get(0).getId()).remove();
        }
    }
    
    /** // 🔹 물리 삭제 대신 논리 삭제 (enabled = false) */
//    public void deleteUser(String username) {
//        List<UserRepresentation> users = tokenService.getKeycloak().realm(realm).users().search(username);
//
//        if (!users.isEmpty()) {
//            String userId = users.get(0).getId();
//            UserResource userResource = tokenService.getKeycloak().realm(realm).users().get(userId);
//            UserRepresentation user = userResource.toRepresentation();
//
//            
//            user.setEnabled(false);
//            userResource.update(user);
//        }
//    }
    
    
    /**
     * Federated Identity에서 카카오 토큰 조회
     */

    public String getKakaoToken(String userId) {
    	String adminToken = tokenService.getAdminToken();
    	System.out.println("adminToken ===========================" + adminToken);
    	String kcId = getUserIdByUsername(userId,adminToken);
    	System.out.println("kcId ===========================" + kcId);
    	WebClient webClient = WebClient.builder().build();
    	List<Map<String, Object>> response = webClient.get()
                .uri(baseUrl + "/admin/realms/" + realm + "/users/" + kcId + "/federated-identity/")
                .header("Authorization", "Bearer " + adminToken)
                .retrieve()
                .bodyToMono(List.class)
                .block();

        if (response != null) {
        	System.out.println("keycloak response ==============" + response.toString());
            for (Map<String, Object> identity : response) {
                if (kakaoIdpAlias.equals(identity.get("identityProvider"))) {
                    return (String) identity.get("token");  // JSON 문자열
                }
            }
        }
        return null;
    }
    
//    public String getUserIdByUsername(String username, String adminToken) {
//    	WebClient webClient = WebClient.builder().build();
//        List<Map<String, Object>> response = webClient.get()
//                .uri("http://keycloak:8080" + "/admin/realms/" + realm + "/users?username=" + username)
//                .header("Authorization", "Bearer " + adminToken)
//                .retrieve()
//                .bodyToMono(List.class)
//                .block();
//
//        if (response != null && !response.isEmpty()) {
//            return (String) response.get(0).get("id");  // ✅ UUID 반환
//        }
//        return null;
//    }
    
    public String getUserIdByUsername(String username, String adminToken) {
    	WebClient webClient = WebClient.builder().build();
        List<Map<String, Object>> response = webClient.get()
                .uri(baseUrl + "/admin/realms/" + realm + "/users?username=" + username)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                .retrieve()
                .bodyToMono(List.class)
                .block();

        if (response != null && !response.isEmpty()) {
            return (String) response.get(0).get("id");  // ✅ UUID 반환
        }
        return null;
    }
    
    
    
    
    //회원탈퇴시 카카오 동의 철회
    public boolean kakaoMemberOut(String userId) {
		
		try {
			String kakaoTokenJson = keycloakFederatedIdentityRepository.findFederatedToken(userId);
			ObjectMapper mapper = new ObjectMapper();
	        KakaoTokenResponse token = mapper.readValue(kakaoTokenJson, KakaoTokenResponse.class);
			long ll = kakaoService.unlinkWithAccessToken(token.getAccessToken());
			if(ll < 0) {
				log.error("카카오 동의철회 에러 user id = {}" ,userId);
			}
		}
		catch(Exception e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}

}
