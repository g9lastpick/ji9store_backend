package com.jjsoft.pos.dto.special;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SpecialReservationDto {
	
	private Long   specialId;                     // 특가 ID
	private Long specialRsvId;                 //예약 미스,터 ID
    private String userId;                      // 유저 ID
    private String tmpUserId;                   // 비로그인 예약자 ID
    private String phoneNo;                     // 예약자 전화번호
    private String visitDate;                   // 수령일

    private List<ReservationItemDto> rows;            // 예약 상품 목록

}
