package com.jjsoft.pos.dto.banner;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;
import lombok.ToString;

/**
 * 띠배너 등록/수정 요청 (multipart/form-data, @ModelAttribute 바인딩)
 * - bannerId 없으면 신규, 있으면 수정
 * - imageFile 없으면 기존 이미지 유지(수정 시)
 * - startDate/endDate 는 "yyyy-MM-dd" 문자열 (기본배너는 비움)
 */
@Data
@ToString
public class StripBannerSaveRequest {

    private Long bannerId;
    private Long storeId;
    private String landingUrl;
    private String startDate;   // yyyy-MM-dd
    private String endDate;     // yyyy-MM-dd
    private Boolean isDefault;
    private Boolean isActive;
    private Integer sortOrder;
    private String title;
    private String createUser;

    @ToString.Exclude
    private MultipartFile imageFile;
}
