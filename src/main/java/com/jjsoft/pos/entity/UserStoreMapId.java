package com.jjsoft.pos.entity;

import java.io.Serializable;

import lombok.*;

/**
 * 유저 매장 매핑 복합키 ID 클래스
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class UserStoreMapId implements Serializable {
    private Long userId;
    private Long storeId;
}
