package com.jjsoft.pos.util;

/**
 * 리뷰 작성자 닉네임 마스킹.
 * 규칙: 가운데 자리를 '*'로 가림.
 *   - 1자: "*"
 *   - 2자: 첫글자 + "*"      예) 김  → 김*
 *   - 3자: 첫글자 + "*" + 끝글자  예) 홍길동 → 홍*동
 *   - 4자 이상: 첫글자 + 중간 모두 "*" + 끝글자  예) 김보미아 → 김**아
 *   - 이메일 형태도 동일 규칙 (로컬파트가 길어도 가운데 전부 '*')
 */
public final class NicknameMaskUtil {

    private NicknameMaskUtil() {}

    public static String mask(String name) {
        if (name == null) return null;
        String s = name.trim();
        if (s.isEmpty()) return s;
        int len = s.length();
        if (len == 1) return "*";
        if (len == 2) return s.charAt(0) + "*";
        StringBuilder sb = new StringBuilder(len);
        sb.append(s.charAt(0));
        for (int i = 1; i < len - 1; i++) sb.append('*');
        sb.append(s.charAt(len - 1));
        return sb.toString();
    }
}
