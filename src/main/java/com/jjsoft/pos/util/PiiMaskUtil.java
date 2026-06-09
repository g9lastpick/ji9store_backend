package com.jjsoft.pos.util;

/** 개인정보 마스킹 유틸. 이름은 NicknameMaskUtil 규칙 재사용. */
public final class PiiMaskUtil {

    private PiiMaskUtil() {}

    public static String maskName(String name) {
        return NicknameMaskUtil.mask(name);
    }

    /** 010-1234-5678 -> 010-****-5678 */
    public static String maskPhone(String phone) {
        if (phone == null) return null;
        String digits = phone.replaceAll("[^0-9]", "");
        if (digits.length() < 7) return "****";
        String first3 = digits.substring(0, 3);
        String last4  = digits.substring(digits.length() - 4);
        return first3 + "-****-" + last4;
    }

    /** abcd@x.com -> a**d@x.com */
    public static String maskEmail(String email) {
        if (email == null) return null;
        int at = email.indexOf('@');
        if (at < 0) return NicknameMaskUtil.mask(email);
        return NicknameMaskUtil.mask(email.substring(0, at)) + email.substring(at);
    }

    /** 1990-01-01 -> 1990-**-** */
    public static String maskBirthday(String birthday) {
        if (birthday == null || birthday.length() < 4) return birthday;
        return birthday.substring(0, 4) + "-**-**";
    }

    /**
     * 계정(로그인 ID) 마스킹. 규칙:
     *  - 이메일 형태(@ 포함)  : a**d@x.com
     *  - 전부 숫자 7자리 이상  : 전화 규칙(010-****-5678)
     *  - 그 외 일반 ID        : 앞 2자 + 가운데 '*' + 끝 1자  예) hong1234 -> ho*****4
     *    (2자 이하: 첫글자+'*', 3~4자: 첫글자+'**'+끝글자)
     */
    public static String maskAccount(String account) {
        if (account == null) return null;
        String s = account.trim();
        if (s.isEmpty()) return s;
        if (s.contains("@")) return maskEmail(s);
        String digits = s.replaceAll("[^0-9]", "");
        if (digits.length() == s.length() && s.length() >= 7) return maskPhone(s);
        int len = s.length();
        if (len <= 2) return s.charAt(0) + "*";
        if (len <= 4) return s.charAt(0) + "**" + s.charAt(len - 1);
        StringBuilder sb = new StringBuilder(len);
        sb.append(s, 0, 2);
        for (int i = 2; i < len - 1; i++) sb.append('*');
        sb.append(s.charAt(len - 1));
        return sb.toString();
    }

    /** 주소: 앞 6자만 노출 */
    public static String maskAddress(String address) {
        if (address == null) return null;
        String s = address.trim();
        if (s.isEmpty()) return s;
        if (s.length() <= 6) return s.charAt(0) + "*****";
        return s.substring(0, 6) + "****";
    }
}
