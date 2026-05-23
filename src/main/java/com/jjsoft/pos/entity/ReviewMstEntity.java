package com.jjsoft.pos.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "review_mst")
@Comment("상품 리뷰")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ReviewMstEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "REVIEW_ID")
    @Comment("리뷰 ID")
    private Long reviewId;

    @Column(name = "PRODUCT_ID", nullable = false)
    @Comment("상품 ID")
    private Long productId;

    @Column(name = "USER_ID", nullable = false, length = 255)
    @Comment("Keycloak sub (UUID)")
    private String userId;

    @Column(name = "USER_NICKNAME", length = 100)
    @Comment("preferred_username 캐시")
    private String userNickname;

    @Column(name = "RATING", nullable = false)
    @Comment("별점 1~5")
    private Integer rating;

    @Column(name = "CONTENT", nullable = false, columnDefinition = "TEXT")
    @Comment("리뷰 본문")
    private String content;

    @Builder.Default
    @Column(name = "HELPFUL_COUNT", nullable = false)
    @Comment("도움됐어요 집계 캐시")
    private Integer helpfulCount = 0;

    @Builder.Default
    @Column(name = "STATUS", nullable = false, length = 20)
    @Comment("ACTIVE / HIDDEN / DELETED")
    private String status = "ACTIVE";

    @CreationTimestamp
    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "UPDATED_AT", nullable = false)
    private LocalDateTime updatedAt;
}
