package com.jjsoft.pos.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.Comment;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import lombok.*;

//@Entity
//@Table(name = "draw_user_stat")
@Comment("드로우 유저 통계")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class DrawUserStatEntity {

    @Id
    @Column(name = "USER_ID", length = 150)
    @Comment("유저 ID")
    private String userId;

    /* =========================
     * 통계 정보
     * ========================= */

    @Column(name = "LAST_1M_PURCHASE_CNT")
    @Comment("최근 1개월 구매 건수")
    private Integer last1mPurchaseCnt;

    @Column(name = "DRAW_WIN_CNT")
    @Comment("드로우 당첨 횟수")
    private Integer drawWinCnt;

    @Column(name = "DRAW_NO_SHOW_CNT")
    @Comment("드로우 미방문 횟수")
    private Integer drawNoShowCnt;

    @Column(name = "LAST_NO_SHOW_DATE")
    @Comment("마지막 미방문 일시")
    private LocalDateTime lastNoShowDate;

    /* =========================
     * 갱신 정보
     * ========================= */

    @UpdateTimestamp
    @Column(name = "UPDATE_DATE")
    @Comment("갱신일자")
    private LocalDateTime updateDate;

    /* =========================
     * 연관관계 (선택, 읽기 전용)
     * ========================= */

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", insertable = false, updatable = false)
    @Comment("유저 정보")
    private UserMstEntity user;
}
