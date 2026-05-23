package com.jjsoft.pos.entity;

import org.hibernate.annotations.Comment;

import jakarta.persistence.*;
import lombok.*;

/**
 * 유저 매장 매핑
 */
@Entity
@Table(name = "USER_STORE_MAP")
@Comment("유저 매장 매핑")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@IdClass(UserStoreMapId.class)
public class UserStoreMapEntity {

    @Id
    @Column(name = "USER_ID")
    @Comment("유저 내부 ID")
    private Long userId;

    @Id
    @Column(name = "STORE_ID")
    @Comment("매장 ID")
    private Long storeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", insertable = false, updatable = false)
    @Comment("유저 정보")
    private UserMstEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "STORE_ID", insertable = false, updatable = false)
    @Comment("매장 정보")
    private StoreMstEntity store;
}
