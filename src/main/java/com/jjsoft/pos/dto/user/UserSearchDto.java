package com.jjsoft.pos.dto.user;

import lombok.Data;

@Data
public class UserSearchDto {
    private Long id;
    private String userId;
    private String ssoId;
    private String password;
    private String name;
    private String email;
    private String phone;
    private String snsType;
    private String gender;
    private String profileImgUrl;
    private String useYn;
    
    /* 픽업현황 조회시 기간 정보 */
    private String startDate;
    private String endDate;
}
