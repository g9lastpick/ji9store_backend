package com.jjsoft.pos.keycloak;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class KakaoUserInfoDto {
    private Long id;                        // 카카오 유저 고유 ID
    private String connected_at;            // 연결 시각 (ISO 형식)
    private Properties properties;          // 유저 프로퍼티 (닉네임 등)
    private KakaoAccount kakao_account;     // 카카오 계정 정보
    private String address;
    @Data
    public static class Properties {
        private String nickname; // 카카오 닉네임
    }

    @Data
    public static class KakaoAccount {
        private boolean profile_nickname_needs_agreement;
        private Profile profile;

        private boolean name_needs_agreement;
        private String name;

        private boolean has_email;
        private boolean email_needs_agreement;

        @JsonProperty("is_email_valid")
        private boolean emailValid;

        @JsonProperty("is_email_verified")
        private boolean emailVerified;

        private String email;

        private boolean has_phone_number;
        private boolean phone_number_needs_agreement;
        private String phone_number;

        private boolean has_age_range;
        private boolean age_range_needs_agreement;
        private String age_range;

        private boolean has_birthyear;
        private boolean birthyear_needs_agreement;
        private String birthyear;

        private boolean has_birthday;
        private boolean birthday_needs_agreement;
        private String birthday;
        private String birthday_type;

        @JsonProperty("is_leap_month")
        private boolean leapMonth;

        private boolean has_gender;
        private boolean gender_needs_agreement;
        private String gender;
        
        
       

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Profile {
            private String nickname;

            @JsonProperty("is_default_nickname")
            private boolean defaultNickname;
        }
    }
}
