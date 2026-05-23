package com.jjsoft.pos.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;

import com.jjsoft.pos.enums.GroupbuyResultType;

import jakarta.persistence.*;
import lombok.*;

//@Entity
//@Table(name = "groupbuy_result_log")
@Comment("공동구매 결과 로그")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class GroupbuyResultLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RESULT_ID")
    @Comment("결과 로그 ID")
    private Long resultId;

    /* =========================
     * FK 컬럼
     * ========================= */

    @Column(name = "GROUPBUY_ID", nullable = false)
    @Comment("공동구매 ID")
    private Long groupbuyId;

    /* =========================
     * 결과 정보
     * ========================= */

    @Enumerated(EnumType.STRING)
    @Column(name = "RESULT_TYPE", length = 20)
    @Comment("결과 (SUCCESS / FAIL / CANCEL)")
    private GroupbuyResultType resultType;

    @CreationTimestamp
    @Column(name = "RESULT_DATE", updatable = false)
    @Comment("결과 일시")
    private LocalDateTime resultDate;

    @Column(name = "DESCRIPTION", length = 1000)
    @Comment("설명")
    private String description;

    /* =========================
     * 연관관계 (읽기 전용)
     * ========================= */

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GROUPBUY_ID", insertable = false, updatable = false)
    @Comment("공동구매 마스터")
    private GroupbuyMstEntity groupbuy;
}
