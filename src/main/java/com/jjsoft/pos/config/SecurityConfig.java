package com.jjsoft.pos.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.Arrays;

import com.jjsoft.pos.keycloak.CustomJwtAuthenticationConverter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final CustomJwtAuthenticationConverter jwtAuthenticationConverter;
	private final com.jjsoft.pos.security.TokenDenylistFilter tokenDenylistFilter;
	
	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowedOriginPatterns(Arrays.asList("*"));
		config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
		config.setAllowedHeaders(Arrays.asList("*"));
		config.setAllowCredentials(true);
		config.setMaxAge(3600L);
		
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", config);
		return source;
	}
	
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
	    http
	        .csrf(AbstractHttpConfigurer::disable)
	        .cors(cors -> cors.configurationSource(corsConfigurationSource()))
	        .authorizeHttpRequests(auth -> auth
	            // 🔓 누구나 접근 가능한 공개 API
	            .requestMatchers(
	                "/health",
	                "/docs/**",
	                "/swagger-ui/**",
	                "/upload/images/**",
	                "/api/public/**",
	                "/api/common/**",
	                "/api/mobile/public/**",
	                "/api/mobile/groupbuy/share-log"
	            ).permitAll()

	            // 🔓 Keycloak 유저 조회
	            .requestMatchers(HttpMethod.GET, "/api/image/**").permitAll()
	            // image write (upload/delete/modify) -> admin only
	            .requestMatchers("/api/image/**").hasAnyRole("ADMIN", "MANAGER")
	            .requestMatchers(HttpMethod.GET, "/api/keycloak/users/**").permitAll()

	            // 🔒 Keycloak 유저 생성/수정/삭제
	            .requestMatchers(HttpMethod.POST, "/api/keycloak/users/**").permitAll() //회원 가입
	            .requestMatchers(HttpMethod.PUT,  "/api/keycloak/users/**").authenticated()
	            .requestMatchers(HttpMethod.DELETE,"/api/keycloak/users/**").authenticated()
	            
	            // 🔒 STAFF 허용 범위: 현장 판매 + 상품 조회 (그 외 관리자 메뉴는 제외)
	            .requestMatchers("/api/admin/sales/**", "/api/admin/products/**")
	                .hasAnyRole("ADMIN", "MANAGER", "SUPER_ADMIN", "STORE_ADMIN", "STAFF")

	            // 🔒 그 외 관리자 전용 API (STAFF 제외, 중복 제거)
	            .requestMatchers("/api/admin/**").hasAnyRole("ADMIN", "MANAGER", "SUPER_ADMIN", "STORE_ADMIN")

	            // 🔒 그 외 모든 요청은 인증 필요
	            .anyRequest().authenticated()
	        )
	        .oauth2ResourceServer(oauth2 -> oauth2
	            .jwt(jwt -> jwt
	                .jwtAuthenticationConverter(jwtAuthenticationConverter)
	            )
	        );

	    // 로그아웃 거부목록(jti) 검사 필터: 인증 통과 후 로그아웃된 토큰이면 401
	    http.addFilterAfter(tokenDenylistFilter,
	        org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter.class);

	    return http.build();
	}

}
