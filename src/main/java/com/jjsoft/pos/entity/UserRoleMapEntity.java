package com.jjsoft.pos.entity;

import org.hibernate.annotations.Comment;

import jakarta.persistence.*;
import lombok.*;

/**
 * 유저 역할 매핑
 */
@Entity
@Table(name = "USER_ROLE_MAP")
@Comment("유저 역할 매핑")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@IdClass(UserRoleMapId.class)
public class UserRoleMapEntity {

    @Id
    @Column(name = "USER_ID")
    @Comment("유저 내부 ID")
    private Long userId;

    @Id
    @Column(name = "ROLE")
    @Comment("역할 (CUSTOMER, STAFF, ADMIN 등)")
    private String role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", insertable = false, updatable = false)
    @Comment("유저 정보")
    private UserMstEntity user;
}
