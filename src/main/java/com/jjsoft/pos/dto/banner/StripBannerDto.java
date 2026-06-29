package com.jjsoft.pos.dto.banner;

import java.time.LocalDate;

import com.jjsoft.pos.entity.SpecialStripBannerEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** 띠배너 응답 DTO (어드민 목록 / 모바일 노출 공용) */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StripBannerDto {

    private Long bannerId;
    private Long storeId;
    private String imageUrl;
    private String landingUrl;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean isDefault;
    private Boolean isActive;
    private Integer sortOrder;
    private String title;

    public static StripBannerDto from(SpecialStripBannerEntity e) {
        return StripBannerDto.builder()
                .bannerId(e.getBannerId())
                .storeId(e.getStoreId())
                .imageUrl(e.getImageUrl())
                .landingUrl(e.getLandingUrl())
                .startDate(e.getStartDate())
                .endDate(e.getEndDate())
                .isDefault(e.getIsDefault())
                .isActive(e.getIsActive())
                .sortOrder(e.getSortOrder())
                .title(e.getTitle())
                .build();
    }
}
