package com.jjsoft.pos.keycloak;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class KeycloakUserDto {

	
	private String id; // ← Keycloak UUID
	/** 사용자명 (Keycloak의 username 필드) */
    private String username;

    /** 이메일 */
    private String email;

    /** 비밀번호 (생성 시 또는 변경 시 사용) */
    private String password;

    /** 사용자 활성화 여부 */
    private Boolean enabled;

    /** 역할 목록 ("admin", "user" 등) */
    private List<String> roles;
}
