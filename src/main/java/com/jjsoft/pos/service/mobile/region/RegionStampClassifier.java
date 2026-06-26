package com.jjsoft.pos.service.mobile.region;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
     * 주소를 7권역 중 하나로 분류한다.
     * @return 권역명(REGIONS 중 하나), 매칭 실패/공백이면 null
     */
    public static String classify(String address) {
        if (address == null) {
            return null;
        }
        String addr = address.trim();
        if (addr.isEmpty()) {
            return null;
        }
        for (Map.Entry<String, String> e : PREFIX_TO_REGION.entrySet()) {
            if (addr.startsWith(e.getKey())) {
                return e.getValue();
            }
        }
        return null;
    }
}
