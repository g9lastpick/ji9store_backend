package com.jjsoft.pos.keycloak;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * 카카오 토큰 발급 응답 DTO
 */
@Data
public class KakaoTokenResponse {

    @JsonProperty("access_token")
    private String accessToken; // 액세스 토큰

    @JsonProperty("token_type")
    private String tokenType; // 토큰 타입 (보통 "bearer")

    @JsonProperty("refresh_token")
    private String refreshToken; // 리프레시 토큰

    @JsonProperty("id_token")
    private String idToken; // OpenID Connect ID Token (있을 경우)

    @JsonProperty("expires_in")
    private Long expiresIn; // 액세스 토큰 만료 시간 (초)

    @JsonProperty("refresh_expires_in")
    private Long refreshExpiresIn; // 리프레시 토큰 만료 시간 (초)

    @JsonProperty("not-before-policy")
    private Long notBeforePolicy; // 토큰 활성화 시점 정책

    @JsonProperty("scope")
    private String scope; // 동의 범위

    @JsonProperty("refresh_token_expires_in")
    private Long refreshTokenExpiresIn; // refresh token 만료 시간

    @JsonProperty("accessTokenExpiration")
    private Long accessTokenExpiration; // access token 만료 시각 (epoch time)
}
