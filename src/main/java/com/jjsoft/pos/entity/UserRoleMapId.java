package com.jjsoft.pos.entity;

import java.io.Serializable;

import lombok.*;

/**
 * UserRoleMap 복합키 ID 클래스
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class UserRoleMapId implements Serializable {
    private Long userId;
    private String role;
}
