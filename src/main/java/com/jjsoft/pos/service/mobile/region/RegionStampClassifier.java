package com.jjsoft.pos.service.mobile.region;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 「지구 한바퀴」 지역 스탬프 - 계열사 주소를 7개 권역으로 분류한다.
 *
 * 규칙(확정):
 *  - 7권역: 서울 / 경기도 / 강원도 / 충청도 / 경상도 / 전라도 / 제주도
 *  - 광역시는 인접 도(道)에 흡수:
 *      인천 → 경기도
 *      대전·세종 → 충청도
 *      대구·부산·울산 → 경상도
 *      광주 → 전라도
 *  - 주소 공백/미매칭은 호출 측에서 집계 제외(여기선 null 반환).
 */
public final class RegionStampClassifier {

    private RegionStampClassifier() {}

    /** 스탬프판 표시 순서(고정). 응답은 항상 이 7개를 이 순서로 채운다. */
    public static final List<String> REGIONS =
            Arrays.asList("서울", "경기도", "강원도", "충청도", "경상도", "전라도", "제주도");

    /**
     * 주소 접두어 → 권역 매핑.
     * "충청"/"경상"/"전라"는 광역도(북도/남도) 양쪽을 한 번에 흡수한다.
     */
    private static final Map<String, String> PREFIX_TO_REGION = buildPrefixMap();

    private static Map<String, String> buildPrefixMap() {
        Map<String, String> m = new LinkedHashMap<>();
        // 서울
        m.put("서울", "서울");
        // 경기도 (+ 인천)
        m.put("경기", "경기도");
        m.put("인천", "경기도");
        // 강원도
        m.put("강원", "강원도");
        // 충청도 (충북/충남 + 대전/세종)
        m.put("충청", "충청도");
        m.put("충북", "충청도");
        m.put("충남", "충청도");
        m.put("대전", "충청도");
        m.put("세종", "충청도");
        // 경상도 (경북/경남 + 대구/부산/울산)
        m.put("경상", "경상도");
        m.put("경북", "경상도");
        m.put("경남", "경상도");
        m.put("대구", "경상도");
        m.put("부산", "경상도");
        m.put("울산", "경상도");
        // 전라도 (전북/전남 + 광주)
        m.put("전라", "전라도");
        m.put("전북", "전라도");
        m.put("전남", "전라도");
        m.put("광주", "전라도");
        // 제주도
        m.put("제주", "제주도");
        return m;
    }

    /**
     * 텍스트(주소 또는 괄호 안 지역 토큰)를 7권역 중 하나로 분류한다.
     * @return 권역명(REGIONS 중 하나), 매칭 실패/공백이면 null
     */
    public static String classify(String text) {
        if (text == null) {
            return null;
        }
        String t = text.trim();
        if (t.isEmpty()) {
            return null;
        }
        for (Map.Entry<String, String> e : PREFIX_TO_REGION.entrySet()) {
            if (t.startsWith(e.getKey())) {
                return e.getValue();
            }
        }
        return null;
    }

    /** 상품명 안의 "(지역명)" 토큰 추출용 */
    private static final Pattern PAREN = Pattern.compile("[\\(（]([^\\)）]*)[\\)）]");

    /**
     * 상품명(PRODUCT_NM)에서 "(지역명)" 형식의 텍스트를 찾아 권역으로 분류한다.
     * 괄호가 여러 개면 권역으로 매칭되는 첫 토큰을 사용한다. 반각/전각 괄호 모두 지원.
     * @return 권역명, 매칭 실패면 null
     */
    public static String fromProductName(String productNm) {
        if (productNm == null || productNm.isEmpty()) {
            return null;
        }
        Matcher m = PAREN.matcher(productNm);
        while (m.find()) {
            String region = classify(m.group(1).trim());
            if (region != null) {
                return region;
            }
        }
        return null;
    }
}
