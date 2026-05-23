package com.jjsoft.pos.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ReturnDto {
	private Long returnId;
    private Long salesId;
    private String storeId;
    private Long userId; // 반품 처리자
    private int totalQty;
    private int totalAmount;
    private String returnReason;
    private String status;
    private LocalDateTime returnDate;

    private LocalDateTime createDate;
    private String createUser;
    private LocalDateTime updateDate;
    private String updateUser;
}
