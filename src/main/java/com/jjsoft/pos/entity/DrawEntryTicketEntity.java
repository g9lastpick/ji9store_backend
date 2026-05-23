package com.jjsoft.pos.entity;

import org.hibernate.annotations.Comment;

import jakarta.persistence.*;
import lombok.*;

//@Entity
//@Table(name = "draw_entry_ticket")
@Comment("드로우 응모 번호(확률 구현)")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class DrawEntryTicketEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TICKET_ID")
    @Comment("티켓 ID")
    private Long ticketId;

    /* =========================
     * FK 컬럼
     * ========================= */

    @Column(name = "DRAW_ID", nullable = false)
    @Comment("드로우 ID")
    private Long drawId;

    @Column(name = "DRAW_ENTRY_ID", nullable = false)
    @Comment("드로우 응모 ID")
    private Long drawEntryId;

    /* =========================
     * 티켓 정보
     * ========================= */

    @Column(name = "TICKET_NO", nullable = false)
    @Comment("응모 번호")
    private Integer ticketNo;

    /* =========================
     * 연관관계 (읽기 전용)
     * ========================= */

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DRAW_ID", insertable = false, updatable = false)
    @Comment("드로우 이벤트")
    private DrawMstEntity draw;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DRAW_ENTRY_ID", insertable = false, updatable = false)
    @Comment("드로우 응모")
    private DrawEntryMstEntity drawEntry;
}
