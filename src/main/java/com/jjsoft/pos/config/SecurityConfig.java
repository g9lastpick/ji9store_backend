package com.jjsoft.pos.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

import com.jjsoft.pos.keycloak.CustomJwtAuthenticationConverter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final CustomJwtAuthenticationConverter jwtAuthenticationConverter;

//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http
//            .csrf(AbstractHttpConfigurer::disable)
//            .authorizeHttpRequests(auth -> auth
//                // 🔓 허용 경로
//                .requestMatchers(
//                    "/health",
//                    "/docs/**",
//                    "/swagger-ui/**",
//                    "/upload/images/**",
//                    "/api/public/**",
//                    "/api/common/**",
//                    "/api/mobile/public/**"
//                   ,"/api/admin/**"// 나중에 로그인 처리되면 주석 처리해야함.
//                   ,"/api/image/**"
//                   ,"/api/keycloak/users/**"
//                    
//                ).permitAll()
//
//                // 🔒 관리자 전용 API
////                .requestMatchers("/api/admin/**").hasRole("ADMIN")
//
//                // 🔒 나머지는 인증 필요
//                .anyRequest().authenticated()
//            )
//            .oauth2ResourceServer(oauth2 -> oauth2
//                .jwt(jwt -> jwt
//                    .jwtAuthenticationConverter(jwtAuthenticationConverter)
//                )
//            );
//
//        return http.build();
//    }
	
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
	    http
	        .csrf(AbstractHttpConfigurer::disable)
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
	                "/api/image/**",
	                "/api/admin/groupbuy/**",
	                "/api/admin/draw/**"
	            ).permitAll()

	            // 🔓 Keycloak 유저 조회는 인증만 필요 (ex: GET)
//	            .requestMatchers(HttpMethod.GET, "/api/keycloak/users/**").authenticated()
	            .requestMatchers(HttpMethod.GET, "/api/keycloak/users/**").permitAll()

	            // 🔒 Keycloak 유저 생성/수정/삭제는 ADMIN 권한 필요
	            .requestMatchers(HttpMethod.POST, "/api/keycloak/users/**").permitAll() //회원 가입.
	            .requestMatchers(HttpMethod.PUT,  "/api/keycloak/users/**").authenticated()
	            .requestMatchers(HttpMethod.DELETE,"/api/keycloak/users/**").authenticated()

	            

	            // 🔒 관리자 전용 API
	            .requestMatchers("/api/admin/**").hasAnyRole("ADMIN" , "MANAGER")
	            .requestMatchers("/api/admin/**").hasRole("ADMIN" )
	            
	            // 🔒 그 외 모든 요청은 인증 필요
	            .anyRequest().authenticated()
	        )
	        .oauth2ResourceServer(oauth2 -> oauth2
	            .jwt(jwt -> jwt
	                .jwtAuthenticationConverter(jwtAuthenticationConverter)
	            )
	        );

	    return http.build();
	}

}
