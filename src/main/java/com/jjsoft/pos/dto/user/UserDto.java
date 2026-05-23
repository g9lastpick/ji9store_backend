package com.jjsoft.pos.dto.user;

import java.util.Map;

import com.jjsoft.pos.dto.special.ReservationItemDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
	
	
	 // 예약 마스터
    private String  userId;
    private String  name;
    private String  phone;
    private String  gender;
    private String  ageRange;
    private String  address;
    private String  createDate;
    private Integer totalPrice;
    private String  birthday;
    private String  age;
    
    /* 픽업현황 관련 변수 */
    private String pickupMonth;
    private Integer totalReservations;
    private Integer pickupComplete;
    private Integer systemCancel;
    private Integer userCancel;
    private Integer userReservations;

	

}
