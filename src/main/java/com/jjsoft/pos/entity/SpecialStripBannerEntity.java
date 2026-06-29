package com.jjsoft.pos.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import lombok.*;

/** 특가 탭 띠배너 */
@Entity
@Table(name = "special_strip_banner")
@Comment("특가 탭 띠배너")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SpecialStripBannerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BANNER_ID")
    private Long bannerId;

    @Column(name = "STORE_ID", nullable = false)
    @Comment("점포 ID")
    private Long storeId;

    @Column(name = "IMAGE_URL", nullable = false, length = 500)
    @Comment("S3 배너 이미지 URL")
    private String imageUrl;

    @Column(name = "LANDING_URL", length = 500)
    @Comment("클릭 랜딩 URL")
    private String landingUrl;

    @Column(name = "START_DATE")
    @Comment("노출 시작일")
    private LocalDate startDate;

    @Column(name = "END_DATE")
    @Comment("노출 종료일(포함)")
    private LocalDate endDate;

    @Builder.Default
    @Column(name = "IS_DEFAULT", nullable = false)
    @Comment("1=기간 외 노출용 기본배너")
    private Boolean isDefault = false;

    @Builder.Default
    @Column(name = "IS_ACTIVE", nullable = false)
    @Comment("1=사용")
    private Boolean isActive = true;

    @Builder.Default
    @Column(name = "SORT_ORDER", nullable = false)
    @Comment("캐러셀 노출 순서(작을수록 먼저)")
    private Integer sortOrder = 1;

    @Column(name = "TITLE", length = 100)
    @Comment("관리용 메모")
    private String title;

    @Column(name = "CREATE_USER", length = 100)
    @Comment("등록자")
    private String createUser;

    @CreationTimestamp
    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "UPDATED_AT", nullable = false)
    private LocalDateTime updatedAt;
}
