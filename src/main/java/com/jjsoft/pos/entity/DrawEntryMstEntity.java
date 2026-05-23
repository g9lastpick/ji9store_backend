package com.jjsoft.pos.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.jjsoft.pos.enums.DrawEntryStatus;

import jakarta.persistence.*;
import lombok.*;

//@Entity
//@Table(name = "draw_entry_mst")
@Comment("드로우 응모 마스터")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class DrawEntryMstEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DRAW_ENTRY_ID")
    @Comment("드로우 응모 ID")
    private Long drawEntryId;


    @Column(name = "DRAW_ID", nullable = false)
    @Comment("드로우 이벤트 ID")
    private Long drawId;

    @Column(name = "USER_ID", nullable = false, length = 150)
    @Comment("유저 ID")
    private String userId;

    @CreationTimestamp
    @Column(name = "ENTRY_DATE", updatable = false)
    @Comment("응모 일시")
    private LocalDateTime entryDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "ENTRY_STATUS", length = 20)
    @Comment("응모 상태 (ENTRY / WIN / LOSE / CANCEL / NOSHOW)")
    private DrawEntryStatus entryStatus;


    @Column(name = "SALES_ID")
    @Comment("구매 완료 시 판매 ID")
    private Long salesId;


    @CreationTimestamp
    @Column(name = "CREATE_DATE", updatable = false)
    @Comment("생성일자")
    private LocalDateTime createDate;

    @UpdateTimestamp
    @Column(name = "UPDATE_DATE")
    @Comment("수정일자")
    private LocalDateTime updateDate;

    /* =========================
     * 연관관계 (읽기 전용)
     * ========================= */

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DRAW_ID", insertable = false, updatable = false)
    @Comment("드로우 이벤트")
    private DrawMstEntity draw;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "USER_ID", insertable = false, updatable = false)
//    @Comment("유저 정보")
//    private UserMstEntity user;
}
