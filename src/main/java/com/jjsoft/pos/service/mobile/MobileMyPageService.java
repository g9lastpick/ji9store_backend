package com.jjsoft.pos.service.mobile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.jjsoft.pos.dto.mobile.PartnerAddressAggDto;
import com.jjsoft.pos.dto.mobile.RegionStampDto;
import com.jjsoft.pos.dto.mobile.RegionStampResponseDto;
import com.jjsoft.pos.mapper.MobileMyPageMapper;
import com.jjsoft.pos.service.mobile.region.RegionStampClassifier;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 모바일 마이페이지 - 「지구 한바퀴」 지역 스탬프.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MobileMyPageService {

    private final MobileMyPageMapper mobileMyPageMapper;

    /** 권역 누적용 내부 집계 홀더 */
    private static class Agg {
        long count;
        LocalDateTime firstDate;
    }

    /**
     * 사용자의 구매내역을 7권역 스탬프로 환산해 반환.
     *
     * @param userId  로그인 사용자 식별자(=email)
     * @param storeId 점포 ID
     */
    public RegionStampResponseDto getRegionStamps(String userId, Long storeId) {

        // 권역별 누적 집계(표시 순서 유지)
        Map<String, Agg> byRegion = new LinkedHashMap<>();
        for (String region : RegionStampClassifier.REGIONS) {
            byRegion.put(region, new Agg());
        }

        List<PartnerAddressAggDto> rows = mobileMyPageMapper.selectPartnerAddressAgg(userId, storeId);
        for (PartnerAddressAggDto row : rows) {
            String region = RegionStampClassifier.classify(row.getAddress());
            if (region == null) {
                // 값은 있으나 7권역 미매칭(해외/오타 등) → 집계 제외 + 로그로 매핑 보정 단서
                log.warn("[지구한바퀴] 권역 미매칭 주소 제외: userId={}, storeId={}, address={}",
                        userId, storeId, row.getAddress());
                continue;
            }
            Agg agg = byRegion.get(region);
            agg.count += row.getCnt();
            if (agg.firstDate == null
                    || (row.getFirstDate() != null && row.getFirstDate().isBefore(agg.firstDate))) {
                agg.firstDate = row.getFirstDate();
            }
        }

        // 7권역 고정 응답 구성
        List<RegionStampDto> stamps = new ArrayList<>();
        int completed = 0;
        for (String region : RegionStampClassifier.REGIONS) {
            Agg agg = byRegion.get(region);
            boolean stamped = agg.count > 0;
            if (stamped) {
                completed++;
            }
            stamps.add(RegionStampDto.builder()
                    .region(region)
                    .stamped(stamped)
                    .count(agg.count)
                    .firstStampedDate(agg.firstDate)
                    .build());
        }

        int total = RegionStampClassifier.REGIONS.size();
        return RegionStampResponseDto.builder()
                .stamps(stamps)
                .completedCount(completed)
                .totalRegions(total)
                .allCompleted(completed == total)
                .build();
    }
}
