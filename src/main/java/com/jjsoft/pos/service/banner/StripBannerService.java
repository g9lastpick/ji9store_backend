package com.jjsoft.pos.service.banner;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.jjsoft.pos.dto.banner.StripBannerDto;
import com.jjsoft.pos.dto.banner.StripBannerSaveRequest;
import com.jjsoft.pos.entity.SpecialStripBannerEntity;
import com.jjsoft.pos.enums.ResponseCode;
import com.jjsoft.pos.exception.GlobalException;
import com.jjsoft.pos.repository.SpecialStripBannerRepository;
import com.jjsoft.pos.service.S3Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/** 특가 탭 띠배너 서비스 */
@Service
@RequiredArgsConstructor
@Log4j2
public class StripBannerService {

    private final SpecialStripBannerRepository bannerRepository;
    private final S3Service s3Service;

    /** 어드민 목록 */
    @Transactional(readOnly = true)
    public List<StripBannerDto> listForAdmin(Long storeId) {
        return bannerRepository.findByStoreIdOrderByIsDefaultDescSortOrderAscCreatedAtDesc(storeId)
                .stream().map(StripBannerDto::from).toList();
    }

    /**
     * 모바일 노출 배너 목록.
     * - 오늘 기간에 드는 활성 배너 전부(캐러셀) 반환
     * - 없으면 기본배너 반환, 그것도 없으면 빈 리스트
     */
    @Transactional(readOnly = true)
    public List<StripBannerDto> getDisplayBanners(Long storeId, LocalDate date) {
        LocalDate today = (date != null) ? date : LocalDate.now();
        List<SpecialStripBannerEntity> list = bannerRepository.findActiveBanners(storeId, today);
        if (list.isEmpty()) {
            list = bannerRepository.findByStoreIdAndIsActiveTrueAndIsDefaultTrueOrderByCreatedAtDesc(storeId);
        }
        return list.stream().map(StripBannerDto::from).toList();
    }

    /** 등록/수정 */
    @Transactional
    public Long save(StripBannerSaveRequest req) {
        if (req.getStoreId() == null) {
            throw new GlobalException(ResponseCode.BAD_REQUEST, "점포 ID(storeId)가 필요합니다.");
        }

        boolean isDefault = Boolean.TRUE.equals(req.getIsDefault());
        LocalDate startDate = parseDate(req.getStartDate());
        LocalDate endDate   = parseDate(req.getEndDate());

        // 일반(기간) 배너는 기간 필수, 시작<=종료
        if (!isDefault) {
            if (startDate == null || endDate == null) {
                throw new GlobalException(ResponseCode.BAD_REQUEST, "기간(시작일/종료일)을 입력하세요. 기간 없는 상시 노출은 '기본배너'로 등록하세요.");
            }
            if (startDate.isAfter(endDate)) {
                throw new GlobalException(ResponseCode.BAD_REQUEST, "시작일이 종료일보다 늦을 수 없습니다.");
            }
        } else {
            // 기본배너는 기간 의미 없음
            startDate = null;
            endDate = null;
        }

        SpecialStripBannerEntity entity;
        if (req.getBannerId() != null) {
            entity = bannerRepository.findById(req.getBannerId())
                    .orElseThrow(() -> new GlobalException(ResponseCode.NOT_FOUND, "배너를 찾을 수 없습니다. id=" + req.getBannerId()));
        } else {
            entity = SpecialStripBannerEntity.builder().storeId(req.getStoreId()).build();
        }

        // 이미지: 신규 파일 있으면 업로드 후 교체. 신규 등록인데 파일 없으면 오류.
        if (req.getImageFile() != null && !req.getImageFile().isEmpty()) {
            String url = s3Service.uploadFile(req.getImageFile());
            entity.setImageUrl(url);
        } else if (entity.getImageUrl() == null) {
            throw new GlobalException(ResponseCode.BAD_REQUEST, "배너 이미지를 등록하세요.");
        }

        entity.setStoreId(req.getStoreId());
        entity.setLandingUrl(StringUtils.hasText(req.getLandingUrl()) ? req.getLandingUrl().trim() : null);
        entity.setStartDate(startDate);
        entity.setEndDate(endDate);
        entity.setIsDefault(isDefault);
        entity.setIsActive(req.getIsActive() == null ? Boolean.TRUE : req.getIsActive());
        entity.setSortOrder(req.getSortOrder() == null ? 1 : req.getSortOrder());
        entity.setTitle(req.getTitle());
        if (StringUtils.hasText(req.getCreateUser())) {
            entity.setCreateUser(req.getCreateUser());
        }

        SpecialStripBannerEntity saved = bannerRepository.save(entity);

        // 기본배너는 점포당 1개만 유지: 방금 저장분 외 나머지 기본배너 해제
        if (isDefault) {
            List<SpecialStripBannerEntity> defaults =
                    bannerRepository.findByStoreIdAndIsActiveTrueAndIsDefaultTrueOrderByCreatedAtDesc(req.getStoreId());
            for (SpecialStripBannerEntity d : defaults) {
                if (!d.getBannerId().equals(saved.getBannerId())) {
                    d.setIsDefault(false);
                    bannerRepository.save(d);
                }
            }
        }
        return saved.getBannerId();
    }

    /** 삭제 */
    @Transactional
    public void delete(Long bannerId) {
        bannerRepository.deleteById(bannerId);
    }

    private LocalDate parseDate(String s) {
        if (!StringUtils.hasText(s)) return null;
        return LocalDate.parse(s.trim());
    }
}
