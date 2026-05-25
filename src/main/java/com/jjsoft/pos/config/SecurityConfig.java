package com.jjsoft.pos.config;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.List;

import com.jjsoft.pos.keycloak.CustomJwtAuthenticationConverter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomJwtAuthenticationConverter jwtAuthenticationConverter;
    private final Environment environment;

    private boolean isDevOrLocalProfile() {
        String[] active = environment.getActiveProfiles();
        if (active.length == 0) {
            active = environment.getDefaultProfiles();
        }
        return Arrays.stream(active)
            .anyMatch(p -> "dev".equalsIgnoreCase(p) || "local".equalsIgnoreCase(p));
    }


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
	    boolean devOrLocal = isDevOrLocalProfile();

	    http
	        .csrf(AbstractHttpConfigurer::disable)
	        .cors(Customizer.withDefaults())
	        .authorizeHttpRequests(auth -> {
	            // 🔓 CORS preflight 모든 OPTIONS 요청 허용
	            auth.requestMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**").permitAll();

	            // 🔓 누구나 접근 가능한 공개 API
	            auth.requestMatchers(
	                "/health",
	                "/upload/images/**",
	                "/api/public/**",
	                "/api/mobile/public/**",
	                "/api/image/**"
	            ).permitAll();

	            // 🔓 docs / swagger-ui: dev·local 만 permitAll, prod 는 ADMIN
	            if (devOrLocal) {
	                auth.requestMatchers("/docs/**", "/swagger-ui/**").permitAll();
	            } else {
	                auth.requestMatchers("/docs/**", "/swagger-ui/**").hasRole("ADMIN");
	            }

	            // 🔒 Keycloak 유저 API: 모든 HTTP 메서드(POST 포함) ADMIN 권한 필요
	            auth.requestMatchers("/api/keycloak/users/**").hasRole("ADMIN");

	            // 🔒 공통 API: runSql 등 백도어 차단 — ADMIN/MANAGER 만 접근
	            auth.requestMatchers("/api/common/**").hasAnyRole("ADMIN", "MANAGER");

	            // 🔒 관리자 전용 API: groupbuy/draw 포함 전체 ADMIN/MANAGER 통일
	            auth.requestMatchers("/api/admin/**").hasAnyRole("ADMIN", "MANAGER");

	            // 🔒 그 외 모든 요청은 인증 필요
	            auth.anyRequest().authenticated();
	        })
	        .oauth2ResourceServer(oauth2 -> oauth2
	            .jwt(jwt -> jwt
	                .jwtAuthenticationConverter(jwtAuthenticationConverter)
	            )
	        );

	    return http.build();
	}


    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cfg = new CorsConfiguration();
        cfg.setAllowedOrigins(List.of(
            "http://3.34.136.152",
            "https://m.ji9store.com",
            "https://admin.ji9store.com",
            "https://jjpos.store",
            "http://3.38.203.50:8501",
            "http://3.38.203.50:8502"
        ));
        cfg.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        cfg.setAllowedHeaders(List.of("*"));
        cfg.setExposedHeaders(List.of("Authorization", "Content-Disposition"));
        cfg.setAllowCredentials(true);
        cfg.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource src = new UrlBasedCorsConfigurationSource();
        src.registerCorsConfiguration("/**", cfg);
        return src;
    }

}
