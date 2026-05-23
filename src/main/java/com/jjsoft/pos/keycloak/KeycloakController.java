package com.jjsoft.pos.keycloak;

import java.util.List;

import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/api/keycloak/users")
@RequiredArgsConstructor
public class KeycloakController {

	
	 private final KeycloakService keycloakUserService;

	    // 🔹 사용자 전체 조회
	 	/**
	     * 🔍 전체 사용자 조회
	     */
	    @GetMapping
	    public ResponseEntity<List<UserRepresentation>> getAllUsers() {
	        return ResponseEntity.ok(keycloakUserService.getAllUsers());
	    }

	    /**
	     * 🔍 조건 검색 (username, email)
	     */
	    @GetMapping("/search")
	    public ResponseEntity<List<UserRepresentation>> searchUsers(
	            @RequestParam(required = false) String username,
	            @RequestParam(required = false) String email) {
	        return ResponseEntity.ok(keycloakUserService.searchUsers(username, email));
	    }

	    /**
	     * 💾 사용자 저장 또는 수정 (username 존재 여부에 따라 분기)
	     */
	    @PostMapping
	    public ResponseEntity<String> saveOrUpdateUser(@RequestBody KeycloakUserDto userDto) {
	        keycloakUserService.saveOrUpdateUser(userDto);
	        return ResponseEntity.ok("사용자 저장 또는 수정 완료");
	    }

	    /**
	     * ❌ 사용자 삭제 (username 기준)
	     */
	    @DeleteMapping("/{username}")
	    public ResponseEntity<String> deleteUser(@PathVariable("username") String username) {
	        keycloakUserService.deleteUser(username);
	        return ResponseEntity.ok("사용자 삭제 완료");
	    }
	    
	    @GetMapping("/kakaoToken/{username}")
	    public ResponseEntity<String> getKakaoToken(
	            @PathVariable("username") String username) {
	        return ResponseEntity.ok(keycloakUserService.getKakaoToken(username));
	    }
	    
}
