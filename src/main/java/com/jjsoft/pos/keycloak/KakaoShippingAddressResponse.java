package com.jjsoft.pos.keycloak;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Data
public class KakaoShippingAddressResponse {
//    @JsonProperty("shipping_addresses")
//    private List<ShippingAddress> shippingAddresses;
//
//
//    @Data
//    public static class ShippingAddress {   // 🔥 static 붙여줘야 함
//        @JsonProperty("region_1depth_name")
//        private String region1depthName;
//
//        @JsonProperty("region_2depth_name")
//        private String region2depthName;
//
//        @JsonProperty("region_3depth_name")
//        private String region3depthName;
//
//        @JsonProperty("address_detail")
//        private String addressDetail;
//
//        @JsonProperty("zone_number")
//        private String zoneNumber;
//    }
	/** 카카오 회원번호 */
    @JsonProperty("user_id")
    private Long userId;

    /** 배송지 목록 */
    @JsonProperty("shipping_addresses")
    private List<ShippingAddress> shippingAddresses;

    /** 추가 동의 필요 여부 */
    @JsonProperty("shipping_addresses_needs_agreement")
    private Boolean shippingAddressesNeedsAgreement;

    @Getter
    @Setter
    @NoArgsConstructor
    @ToString
    public static class ShippingAddress {
        @JsonProperty("id")                       private Long    id;
        @JsonProperty("name")                     private String  name;
        @JsonProperty("is_default")               private Boolean isDefault;
        @JsonProperty("updated_at")               private Integer updatedAt;
        @JsonProperty("type")                     private String  type;
        @JsonProperty("base_address")             private String  baseAddress;        // 기본 주소
        @JsonProperty("detail_address")           private String  detailAddress;      // 상세 주소
        @JsonProperty("receiver_name")            private String  receiverName;       // 수령인
        @JsonProperty("receiver_phone_number1")   private String  receiverPhoneNumber1;
        @JsonProperty("receiver_phone_number2")   private String  receiverPhoneNumber2;
        @JsonProperty("zone_number")              private String  zoneNumber;         // 신주소 5자리
        @JsonProperty("zip_code")                 private String  zipCode;            // (구주소) 6자리일 수 있음
    }
}