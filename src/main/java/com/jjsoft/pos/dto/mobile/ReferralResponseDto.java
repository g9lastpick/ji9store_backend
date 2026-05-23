package com.jjsoft.pos.dto.mobile;

import jakarta.validation.constraints.NotNull;
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
public class ReferralResponseDto {

	   /* 추천인 이름 */
    private String name;

    /* 추천인 전화번호 */
    private String phone;

    /* 추천 받은 횟수 */
    private int referralCount;
}
