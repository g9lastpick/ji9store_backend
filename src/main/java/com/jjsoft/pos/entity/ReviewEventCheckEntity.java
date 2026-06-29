package com.jjsoft.pos.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import lombok.*;

/** 리뷰 유저별 이벤트 참여 체크 (Keycloak sub 단위) */
@Entity
@Table(name = "review_event_check")
@Comment("리뷰 유저별 이벤트 참여 체크")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReviewEventCheckEntity {

    @Id
    @Column(name = "USER_ID", length = 255)
    @Comment("리뷰 작성자 Keycloak sub")
    private String userId;

    @Builder.Default
    @Column(name = "PARTICIPATED", nullable = false)
    @Comment("1=이벤트 참여 체크")
    private Boolean participated = false;

    @Column(name = "UPDATE_USER", length = 100)
    @Comment("체크한 관리자")
    private String updateUser;

    @CreationTimestamp
    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "UPDATED_AT", nullable = false)
    private LocalDateTime updatedAt;
}
