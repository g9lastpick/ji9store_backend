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

    /** 주소: 앞 6자만 노출 */
    public static String maskAddress(String address) {
        if (address == null) return null;
        String s = address.trim();
        if (s.isEmpty()) return s;
        if (s.length() <= 6) return s.charAt(0) + "*****";
        return s.substring(0, 6) + "****";
    }
}
