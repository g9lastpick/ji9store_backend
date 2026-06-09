package com.jjsoft.pos.util;

import java.util.List;

import com.jjsoft.pos.dto.user.UserDto;
import com.jjsoft.pos.dto.user.UserSearchDto;

/**
 * 어드민 응답에 실리는 고객 PII 일괄 마스킹 + 인구통계/민감 컬럼 제거.
 *  - 이름   : 가운데 자리 마스킹 (NicknameMaskUtil 규칙)
 *  - 전화   : 010-****-5678
 *  - 계정/SSO/이메일 : PiiMaskUtil.maskAccount / maskEmail
 *  - 성별·주소·나이·생년월일 : 응답에서 제거(null)
 *  - 비밀번호 : 응답에서 제거(null)
 *
 * 화면(ag-grid)이 응답 데이터를 그대로 엑셀로 내보내므로, 응답 단계에서
 * 마스킹하면 화면·엑셀이 동시에 가려진다.
 */
public final class UserPiiMasker {

    private UserPiiMasker() {}

    /** 읽기전용 UserDto 리스트(회원조회·픽업현황) 마스킹 */
    public static List<UserDto> maskUserDtoList(List<UserDto> list) {
        if (list == null) return null;
        for (UserDto d : list) maskUserDto(d);
        return list;
    }

    public static void maskUserDto(UserDto d) {
        if (d == null) return;
        d.setUserId(PiiMaskUtil.maskAccount(d.getUserId()));
        d.setName(PiiMaskUtil.maskName(d.getName()));
        d.setPhone(PiiMaskUtil.maskPhone(d.getPhone()));
        // 인구통계 정보는 어드민 노출 금지 → 제거
        d.setGender(null);
        d.setAddress(null);
        d.setAge(null);
        d.setAgeRange(null);
        d.setBirthday(null);
    }

    /** 회원 검색/편집 그리드 UserSearchDto 리스트 마스킹 */
    public static List<UserSearchDto> maskSearchDtoList(List<UserSearchDto> list) {
        if (list == null) return null;
        for (UserSearchDto d : list) maskSearchDto(d);
        return list;
    }

    public static void maskSearchDto(UserSearchDto d) {
        if (d == null) return;
        d.setUserId(PiiMaskUtil.maskAccount(d.getUserId()));
        d.setSsoId(PiiMaskUtil.maskAccount(d.getSsoId()));
        d.setName(PiiMaskUtil.maskName(d.getName()));
        d.setPhone(PiiMaskUtil.maskPhone(d.getPhone()));
        d.setEmail(PiiMaskUtil.maskEmail(d.getEmail()));
        // 민감/불필요 필드 제거
        d.setPassword(null);
        d.setGender(null);
    }
}
