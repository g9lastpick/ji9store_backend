package com.jjsoft.pos.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "review_reaction")
@Comment("리뷰 도움됐어요/좋아요 원본")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ReviewReactionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "REACTION_ID")
    private Long reactionId;

    @Column(name = "REVIEW_ID", nullable = false)
    private Long reviewId;

    @Column(name = "USER_ID", nullable = false, length = 255)
    @Comment("Keycloak sub")
    private String userId;

    @Builder.Default
    @Column(name = "REACTION_TYPE", nullable = false, length = 20)
    @Comment("HELPFUL (확장: LIKE 등)")
    private String reactionType = "HELPFUL";

    @CreationTimestamp
    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
