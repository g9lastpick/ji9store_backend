package com.jjsoft.pos.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
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
    private final Environment env;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        boolean docsOpen = env.acceptsProfiles(Profiles.of("dev", "local"));

        http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> {
                auth
                    // 🔓 공개 엔드포인트
                    .requestMatchers(
                        "/health",
                        "/upload/images/**",
                        "/api/public/**",
                        "/api/mobile/public/**",
                        "/api/image/**"
                    ).permitAll();

                // 🔓/🔒 docs·swagger: dev/local 에서만 공개, 그 외(prod 포함) ADMIN
                if (docsOpen) {
                    auth.requestMatchers("/docs/**", "/swagger-ui/**").permitAll();
                } else {
                    auth.requestMatchers("/docs/**", "/swagger-ui/**").hasRole("ADMIN");
                }

                auth
                    // 🔒 Keycloak 사용자 API: 전체 메서드(POST 포함) ADMIN
                    .requestMatchers("/api/keycloak/users/**").hasRole("ADMIN")

                    // 🔒 공통 API (runSql 등 백도어 경로 차단)
                    .requestMatchers("/api/common/**").hasAnyRole("ADMIN", "MANAGER")

                    // 🔒 관리자 API 단일 매처
                    .requestMatchers("/api/admin/**").hasAnyRole("ADMIN", "MANAGER")

                    // 🔒 그 외는 인증 필요
                    .anyRequest().authenticated();
            })
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt
                    .jwtAuthenticationConverter(jwtAuthenticationConverter)
                )
            );

        return http.build();
    }
}
