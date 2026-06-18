package com.jjsoft.pos.service.admin.groupbuy;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jjsoft.pos.dto.groupbuy.GroupbuyDetailResponseDto;
import com.jjsoft.pos.dto.groupbuy.GroupbuyPriceStepDto;
import com.jjsoft.pos.dto.groupbuy.GroupbuyRequestDto;
import com.jjsoft.pos.dto.groupbuy.GroupbuyResponseDto;
import com.jjsoft.pos.dto.groupbuy.GroupbuySearchRequestDto;
import com.jjsoft.pos.entity.GroupbuyJoinMstEntity;
import com.jjsoft.pos.entity.GroupbuyMstEntity;
import com.jjsoft.pos.entity.GroupbuyPriceStepEntity;
import com.jjsoft.pos.enums.GroupbuyJoinStatus;
import com.jjsoft.pos.enums.GroupbuyPickupMode;
import com.jjsoft.pos.enums.GroupbuyStatus;
import com.jjsoft.pos.enums.ResponseCode;
import com.jjsoft.pos.exception.GlobalException;
import com.jjsoft.pos.mapper.GroupbuyAdminMapper;
import com.jjsoft.pos.repository.GroupbuyJoinMstRepository;
import com.jjsoft.pos.repository.GroupbuyMstRepository;
import com.jjsoft.pos.repository.GroupbuyPriceStepRepository;
import com.jjsoft.pos.service.admin.pos.PosPaymentService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 * 공동구매 관리자 Service
 */
@Service
@RequiredArgsConstructor
@Log4j2
@Transactional
public class GroupbuyAdminService {

    private final GroupbuyMstRepository       groupbuyMstRepository;
    private final GroupbuyJoinMstRepository   groupbuyJoinMstRepository;
    private final GroupbuyPriceStepRepository groupbuyPriceStepRepository;

    /** 복잡 조회 전용 */
    private final GroupbuyAdminMapper         groupbuyAdminMapper;

    /** POS 결제 연동 */
    private final PosPaymentService           posPaymentService;


    /* =========================================================
     * 공동구매 목록 조회
     * ========================================================= */
    @Transactional(readOnly = true)
    public List<GroupbuyResponseDto> getGroupbuyList(GroupbuySearchRequestDto requestDto) {
        return groupbuyAdminMapper.selectGroupbuyList(requestDto);
    }

    /* =========================================================
     * 공동구매 상세 조회
     * ========================================================= */
    @Transactional(readOnly = true)
    public GroupbuyDetailResponseDto getGroupbuyDetail(Long groupbuyId) {
        return groupbuyAdminMapper.selectGroupbuyDetail(groupbuyId);
    }

    /* =========================================================
     * 공동구매 예약자(참여자) 목록 조회 — PII 마스킹 적용
     * ========================================================= */
    @Transactional(readOnly = true)
    public List<com.jjsoft.pos.dto.groupbuy.GroupbuyJoinResponseDto> getGroupbuyJoinList(Long groupbuyId, String joinStatus) {
        List<com.jjsoft.pos.dto.groupbuy.GroupbuyJoinResponseDto> list = groupbuyAdminMapper.selectGroupbuyJoinList(groupbuyId, joinStatus);
        if (list != null) {
            for (com.jjsoft.pos.dto.groupbuy.GroupbuyJoinResponseDto r : list) {
                r.setUserNm(com.jjsoft.pos.util.PiiMaskUtil.maskName(r.getUserNm()));
                r.setUserId(com.jjsoft.pos.util.PiiMaskUtil.maskEmail(r.getUserId()));
                r.setTmpPhoneNo(com.jjsoft.pos.util.PiiMaskUtil.maskPhone(r.getTmpPhoneNo()));
            }
        }
        return list;
    }

    /* =========================================================
     * 모바일 공동구매 활성 목록 조회
     * ========================================================= */
    @Transactional(readOnly = true)
    public List<GroupbuyResponseDto> getMobileGroupbuyList(Long storeId, Long locationId) {
        return groupbuyAdminMapper.selectMobileGroupbuyList(storeId, locationId);
    }

    /* =========================================================
     * 모바일 마이페이지 - 내 공동구매 예약 목록 (본인 데이터, 마스킹 없음)
     * ========================================================= */
    @Transactional(readOnly = true)
    public List<com.jjsoft.pos.dto.groupbuy.GroupbuyJoinResponseDto> getMyGroupbuyList(String userId, Long storeId) {
        return groupbuyAdminMapper.selectMyGroupbuyList(userId, storeId);
    }


    /* =========================================================
     * 공동구매 등록
     * ========================================================= */
    @Transactional
    public Long createGroupbuy(GroupbuyRequestDto dto) {
        //공동구매 가격 정책 검증
        validatePriceSteps(dto.getPriceList(), dto.getTargetQty());

        GroupbuyPickupMode pickupMode = (dto.getPickupMode() == null) ? GroupbuyPickupMode.MANUAL : dto.getPickupMode();
        boolean autoPickup = pickupMode == GroupbuyPickupMode.AUTO;

        GroupbuyMstEntity entity = GroupbuyMstEntity.builder()
                .storeId      (dto.getStoreId())
                .locationId   (dto.getLocationId())
                .productId    (dto.getProductId())
                .groupbuyNm   (dto.getGroupbuyNm())
                .groupbuyType (dto.getGroupbuyType())
                .targetQty    (dto.getTargetQty())
                .targetAmount (dto.getTargetAmount())
                .limitQty     (dto.getLimitQty())
                .currentQty   (0)
                .currentAmount(0)
                .startDate    (dto.getStartDate())
                .endDate      (dto.getEndDate())
                .pickupMode      (pickupMode)
                .pickupStartDate (autoPickup ? null : dto.getPickupStartDate())
                .pickupEndDate   (autoPickup ? null : dto.getPickupEndDate())
                .payType      (dto.getPayType())
                .description  (dto.getDescription())
                .status       (GroupbuyStatus.READY)
                .createUser   (dto.getCreateUser())
                .build();

        groupbuyMstRepository.save(entity);

        /** 가격 정책 저장 */
        for (GroupbuyPriceStepDto priceDto : dto.getPriceList()) {

            GroupbuyPriceStepEntity stepEntity = GroupbuyPriceStepEntity.builder()
                    .groupbuyId  (entity.getGroupbuyId())
                    .stepQtyFrom (priceDto.getStepQtyFrom())
                    .stepQtyTo   (priceDto.getStepQtyTo())
                    .salesPrice  (priceDto.getSalesPrice())
                    .salesRate   (priceDto.getSalesRate())
                    .createUser  (dto.getCreateUser())
                    .build();

            groupbuyPriceStepRepository.save(stepEntity);
        }

        log.info("[GROUPBUY][CREATE] id={}", entity.getGroupbuyId());

        return entity.getGroupbuyId();
    }

    /* =========================================================
     * 공동구매 수정 : READY 상태에서만 가능
     * ========================================================= */
    @Transactional
    public void updateGroupbuy(Long groupbuyId, GroupbuyRequestDto dto) {

        GroupbuyMstEntity entity = groupbuyMstRepository.findById(groupbuyId)
                .orElseThrow(() -> new GlobalException(ResponseCode.NOT_FOUND_OBJECT, "공동구매가 존재하지 않습니다."));

        if (entity.getStatus() != GroupbuyStatus.READY) {
            log.info("[GROUPBUY][UPDATE][DENY] groupbuyId={} status={}",
                     groupbuyId, entity.getStatus());
            throw new GlobalException(ResponseCode.BAD_REQUEST, "오픈 전(대기) 상태의 공동구매만 수정할 수 있습니다.");
        }

        //공동구매 가격 정책 검증
        validatePriceSteps(dto.getPriceList(), dto.getTargetQty());

        /** 마스터 정보 수정 */
        entity.setStoreId        (dto.getStoreId());
        entity.setLocationId     (dto.getLocationId());
        entity.setProductId      (dto.getProductId());
        entity.setGroupbuyNm     (dto.getGroupbuyNm());
        entity.setGroupbuyType   (dto.getGroupbuyType());
        entity.setTargetQty      (dto.getTargetQty());
        entity.setTargetAmount   (dto.getTargetAmount());
        entity.setLimitQty       (dto.getLimitQty());
        entity.setStartDate      (dto.getStartDate());
        entity.setEndDate        (dto.getEndDate());
        GroupbuyPickupMode pickupMode = (dto.getPickupMode() == null) ? GroupbuyPickupMode.MANUAL : dto.getPickupMode();
        entity.setPickupMode(pickupMode);
        if (pickupMode == GroupbuyPickupMode.AUTO) {
            entity.setPickupStartDate(null);
            entity.setPickupEndDate(null);
        } else {
            entity.setPickupStartDate(dto.getPickupStartDate());
            entity.setPickupEndDate  (dto.getPickupEndDate());
        }
        entity.setPayType        (dto.getPayType());
        entity.setStatus         (dto.getStatus());
        entity.setDescription    (dto.getDescription());
        entity.setUpdateUser     (dto.getUpdateUser());

        /** 기존 가격 정책 전체 삭제 */
        groupbuyPriceStepRepository.deleteByGroupbuyId(groupbuyId);
        groupbuyPriceStepRepository.flush();//즉시반영

        /** 가격 정책 재등록 */
        for (GroupbuyPriceStepDto priceDto : dto.getPriceList()) {

            GroupbuyPriceStepEntity stepEntity = GroupbuyPriceStepEntity.builder()
                    .groupbuyId  (groupbuyId)
                    .stepQtyFrom (priceDto.getStepQtyFrom())
                    .stepQtyTo   (priceDto.getStepQtyTo())
                    .salesPrice  (priceDto.getSalesPrice())
                    .salesRate   (priceDto.getSalesRate())
                    .createUser  (dto.getUpdateUser())
                    .build();

            groupbuyPriceStepRepository.save(stepEntity);
        }

        log.info("[GROUPBUY][UPDATE] id={}", groupbuyId);
    }

    /* =========================================================
     * 공동구매 삭제 (CANCEL 처리) - 삭제는 status를 cancel로 변경 :
     *   참여자 있는경우 변경 불가 ,
     *   이미 종료 취소 된경우 변경 불가
     *   가격 정책은 테이블에서 실재 삭제
     * ========================================================= */
    @Transactional
    public void cancelGroupbuy(Long groupbuyId) {

        GroupbuyMstEntity entity = groupbuyMstRepository.findById(groupbuyId)
                .orElseThrow(() -> new GlobalException(ResponseCode.NOT_FOUND_OBJECT, "공동구매가 존재하지 않습니다."));

        // 이미 종료/취소된 경우 방어
        if (entity.getStatus() == GroupbuyStatus.CANCEL
            || entity.getStatus() == GroupbuyStatus.END) {

            log.info("[GROUPBUY][CANCEL][SKIP] id={} status={}",
                     groupbuyId, entity.getStatus());
            return;
        }

        boolean hasActiveJoin = groupbuyJoinMstRepository.existsByGroupbuyIdAndJoinStatusIn(
                groupbuyId,
                java.util.List.of(GroupbuyJoinStatus.JOIN, GroupbuyJoinStatus.PAYED));
        if (hasActiveJoin) {

            throw new GlobalException(ResponseCode.BAD_REQUEST, "예약한 고객이 있는 공동구매는 삭제할 수 없습니다.");
        }

        /** 상태 변경 */
        entity.setStatus(GroupbuyStatus.CANCEL);

        /** 가격 정책 삭제 */
        groupbuyPriceStepRepository.deleteByGroupbuyId(groupbuyId);

        log.info("[GROUPBUY][CANCEL] id={}", groupbuyId);
    }


    /**
     * 공동구매 가격 정책 검증
     */
    private void validatePriceSteps(List<GroupbuyPriceStepDto> priceList, Integer targetQty) {

        if (priceList == null || priceList.isEmpty()) {
            throw new GlobalException(ResponseCode.BAD_REQUEST, "가격 정책은 최소 1개 이상 필요합니다.");
        }

        // 1. stepQtyFrom 기준 정렬
        List<GroupbuyPriceStepDto> sorted =
                priceList.stream()
                         .sorted(Comparator.comparing(GroupbuyPriceStepDto::getStepQtyFrom))
                         .toList();

        Integer prevTo = null;

        for (GroupbuyPriceStepDto step : sorted) {

            Integer from = step.getStepQtyFrom();
            Integer to   = step.getStepQtyTo();

            // 2. 기본 값 검증
            if (from == null || to == null) {

                throw new GlobalException(ResponseCode.BAD_REQUEST, "수량 구간은 필수입니다.");
            }

            if (from <= 0 || to <= 0) {

                throw new GlobalException(ResponseCode.BAD_REQUEST, "수량 구간은 1 이상이어야 합니다.");
            }

            if (from > to) {

                throw new GlobalException(ResponseCode.BAD_REQUEST, "수량 시작은 종료보다 클 수 없습니다.");
            }

            // 3. 이전 구간과 겹침 검사
            if (prevTo != null) {
                if (from <= prevTo) {

                    throw new GlobalException(ResponseCode.BAD_REQUEST
                                            , String.format("가격 정책 수량 구간이 겹칩니다. 이전 종료=%d, 현재 시작=%d", prevTo, from));
                }
            }

            // 4. targetQty 범위 초과 여부
            if (targetQty != null && to > targetQty) {

                throw new GlobalException(ResponseCode.BAD_REQUEST
                        , String.format("가격 정책 종료 수량(%d)이 목표 수량(%d)을 초과합니다.", to, targetQty));
            }

            prevTo = to;
        }
    }


    /*****************************************************************************
     * 공동구매 참여 로직 start
     * 모바일 controller 쪽에서도 해당 함수 호출하여 admin과 user가 동시에 사용 할 수 있는 참여 및 수정 취소 시스템
     * 참여 후 변경 수량이 0 이면 참여 취소로 판단함.
     * ****************************************************************************
     * */
    @Transactional
    public void enterGroupbuy( Long groupbuyId, String userId, int joinQty ,GroupbuyJoinStatus requestStatus) {
        enterGroupbuy(groupbuyId, userId, joinQty, requestStatus, false);
    }

    /** addQty=true면 기존 예약수량에 joinQty 누적(모바일 카드), false면 절대 총량 세팅(마이페이지/어드민) */
    @Transactional
    public void enterGroupbuy( Long groupbuyId, String userId, int joinQty ,GroupbuyJoinStatus requestStatus, boolean addQty) {

        /* 1. 기존 예약 조회 (JOIN / PAYED 모두 조회) */
        Optional<GroupbuyJoinMstEntity> optionalJoin =
                groupbuyJoinMstRepository.findByGroupbuyIdAndUserId(groupbuyId, userId);

        /* 2. 취소 요청 */
        if (requestStatus == GroupbuyJoinStatus.CANCEL) {

            if (optionalJoin.isEmpty()) {

                throw new GlobalException(ResponseCode.NOT_FOUND_OBJECT , "취소할 예약이 없습니다.");
            }

            handleCancelJoin(groupbuyId, optionalJoin.get());
            return;
        }

        /* 3. 참여 / 수량 변경 */
        if (optionalJoin.isPresent()) {
            handleExistingJoin(groupbuyId, optionalJoin.get(), joinQty, addQty);
        } else {
            handleNewJoin(groupbuyId, userId, joinQty);
        }

        /* 3-1. 딜성공(SUCCESS) 딜 재예약: 신규/변경 join 에 성공가를 고정해 가격 역전(현재수량 기준 재계산) 방지 */
        groupbuyMstRepository.findById(groupbuyId).ifPresent(gbAfter -> {
            if (gbAfter.getStatus() == GroupbuyStatus.SUCCESS) {
                Integer successPrice = groupbuyAdminMapper.selectGroupbuySuccessUnitPrice(groupbuyId);
                if (successPrice != null && successPrice > 0) {
                    groupbuyJoinMstRepository.findByGroupbuyIdAndUserId(groupbuyId, userId).ifPresent(j -> {
                        j.setUnitPrice(successPrice);
                        groupbuyJoinMstRepository.save(j);
                    });
                }
            }
        });

        /* 4. 자동 픽업 모드: 첫 급간(최소수량) 달성 시 픽업창(now ~ 당일 20시) 자동 설정 */
        maybeAutoStartPickup(groupbuyId);

        /* 5. 최대 급간 최대수량 조기 달성 시 종료시간 전이라도 딜 즉시 종료(SUCCESS) */
        maybeCloseGroupbuyEarly(groupbuyId);
    }

    /** 자동 픽업 시작 처리 — AUTO 모드 + 첫 급간 달성 + 미시작 시 픽업창(now ~ 당일 20시) 설정 */
    private void maybeAutoStartPickup(Long groupbuyId) {
        /* increaseCurrentQty가 raw SQL이라 JPA 1차캐시 currentQty는 stale → DB값 기준 SQL로 직접 판정/세팅 */
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        java.time.LocalDateTime end = now.toLocalDate().atTime(20, 0);
        int updated = groupbuyAdminMapper.autoStartPickup(groupbuyId, now, end);
        if (updated > 0) {
            log.info("[GROUPBUY][AUTO-PICKUP][START] id={} window={}~{}", groupbuyId, now, end);
        }
    }

    /**
     * 최대 급간 최대수량(MAX STEP_QTY_TO) 조기 달성 시 종료시간 전이라도 딜을 즉시 SUCCESS 종료.
     * increaseCurrentQty가 raw SQL이라 JPA 1차캐시는 stale → DB current_qty 기준 SQL로 판정.
     */
    private void maybeCloseGroupbuyEarly(Long groupbuyId) {
        int closed = groupbuyAdminMapper.closeGroupbuyEarlyIfMaxReached(groupbuyId);
        if (closed > 0) {
            Integer unitPrice = groupbuyAdminMapper.selectTopStepSalesPrice(groupbuyId);
            if (unitPrice != null) {
                groupbuyAdminMapper.updateJoinSuccess(groupbuyId, unitPrice);
            }
            log.info("[GROUPBUY][EARLY-CLOSE][SUCCESS] id={} unitPrice={}", groupbuyId, unitPrice);
        }
    }

    /** 공동구매 신규 참여 */
    private void handleNewJoin(Long groupbuyId, String userId, int joinQty) {

        if (joinQty <= 0) {

            throw new GlobalException(ResponseCode.BAD_REQUEST , "참여 수량은 1 이상이어야 합니다.");
        }

        validatePerPersonLimit(groupbuyId, joinQty);
        validateGroupbuyJoin(groupbuyId, joinQty);

        GroupbuyJoinMstEntity join = GroupbuyJoinMstEntity.builder()
                .groupbuyId(groupbuyId)
                .userId    (userId)
                .joinStatus(GroupbuyJoinStatus.JOIN)
                .totalQty  (joinQty)
                .build();

        groupbuyJoinMstRepository.save(join);

        applyGroupbuyJoinResult(groupbuyId, joinQty);
    }

    /** 참여자 수정 */
    private void handleExistingJoin( Long groupbuyId, GroupbuyJoinMstEntity existingJoin, int reqQty, boolean addQty ) {

        /* 결제 완료 건은 변경 불가 */
        if (existingJoin.getJoinStatus() == GroupbuyJoinStatus.PAYED) {

            throw new GlobalException(ResponseCode.BAD_REQUEST , "결제 완료된 예약은 변경할 수 없습니다.");
        }

        int oldQty = existingJoin.getTotalQty();
        /* addQty=true(카드 '예약하기')면 기존 수량에 누적, false(마이페이지)면 절대 총량 */
        int newQty = addQty ? (oldQty + reqQty) : reqQty;
        int diffQty = newQty - oldQty;

        /* 수량 0 → 취소 */
        if (newQty == 0) {
            handleCancelJoin(groupbuyId, existingJoin);
            return;
        }

        /* 이미 동일 수량으로 예약된 상태에서의 중복 요청 차단 (카드 '예약하기' 반복 클릭) */
        if (existingJoin.getJoinStatus() == GroupbuyJoinStatus.JOIN && diffQty == 0) {
            Integer limitQty = groupbuyMstRepository.findById(groupbuyId)
                    .map(GroupbuyMstEntity::getLimitQty).orElse(null);
            int maxQty = (limitQty != null && limitQty > 0) ? limitQty : oldQty;
            throw new GlobalException(ResponseCode.BAD_REQUEST , "1인당 최대 " + maxQty + "개까지 예약할 수 있습니다.");
        }

        /* 1인당 구매 제한 검증 (총 수량 기준) */
        validatePerPersonLimit(groupbuyId, newQty);

        /* 수량 변경 */
        if (diffQty != 0) {

            if (diffQty > 0) {
                validateGroupbuyJoin(groupbuyId, diffQty);
                applyGroupbuyJoinResult(groupbuyId, diffQty);
            } else {
                /* 딜성공(SUCCESS) 부분취소: 달성 급간 최소수량(M) 미만으로는 축소 불가 */
                GroupbuyMstEntity gbDec = groupbuyMstRepository.findById(groupbuyId).orElse(null);
                if (gbDec != null && gbDec.getStatus() == GroupbuyStatus.SUCCESS) {
                    Integer minQty = groupbuyAdminMapper.selectAchievedMinQty(groupbuyId);
                    int m = (minQty != null) ? minQty : 0;
                    int curQty = (gbDec.getCurrentQty() == null) ? 0 : gbDec.getCurrentQty();
                    if (curQty + diffQty < m) {
                        throw new GlobalException(ResponseCode.BAD_REQUEST,
                                "성공한 공동구매의 달성 최소 수량(" + m + "개) 미만으로는 취소할 수 없습니다.");
                    }
                }
                applyGroupbuyCancelResult(groupbuyId, Math.abs(diffQty));
            }

            existingJoin.setTotalQty(newQty);
        }
        existingJoin.setJoinStatus(GroupbuyJoinStatus.JOIN);
        groupbuyJoinMstRepository.save(existingJoin);
    }

    /** 공동구매 참여 취소 */
    private void handleCancelJoin( Long groupbuyId, GroupbuyJoinMstEntity existingJoin ) {

        /* 공동구매 상태 검증 (취소 가능 여부) */
        GroupbuyMstEntity groupbuy =
            groupbuyMstRepository.findById(groupbuyId)
                .orElseThrow(() ->
                    new GlobalException(ResponseCode.NOT_FOUND_OBJECT, "공동구매 정보가 없습니다.")
                );

        GroupbuyStatus st = groupbuy.getStatus();
        /* 진행중(START) 또는 딜성공(SUCCESS) 상태에서만 취소 허용 */
        if (st != GroupbuyStatus.START && st != GroupbuyStatus.SUCCESS) {
            throw new GlobalException(ResponseCode.BAD_REQUEST, "취소 가능한 상태가 아닙니다.");
        }

        /* 결제 완료(픽업완료) 건은 취소 불가 */
        if (existingJoin.getJoinStatus() == GroupbuyJoinStatus.PAYED) {

            throw new GlobalException(ResponseCode.BAD_REQUEST , "결제 완료된 예약은 취소할 수 없습니다.");
        }

        /* 이미 취소된 경우 */
        if (existingJoin.getJoinStatus() == GroupbuyJoinStatus.CANCEL) {
            return;
        }

        int cancelQty = existingJoin.getTotalQty();

        /* 딜성공(SUCCESS): 달성 급간 최소수량(M) 미만으로는 취소 불가 → 달성 가격단계·성공가 보존 */
        if (st == GroupbuyStatus.SUCCESS) {
            Integer minQty = groupbuyAdminMapper.selectAchievedMinQty(groupbuyId);
            int m = (minQty != null) ? minQty : 0;
            int curQty = (groupbuy.getCurrentQty() == null) ? 0 : groupbuy.getCurrentQty();
            if (curQty - cancelQty < m) {
                throw new GlobalException(ResponseCode.BAD_REQUEST,
                        "성공한 공동구매의 달성 최소 수량(" + m + "개) 미만으로는 취소할 수 없습니다.");
            }
        }

        /* 상태 변경 */
        existingJoin.setJoinStatus(GroupbuyJoinStatus.CANCEL);
        existingJoin.setTotalQty(0);
        groupbuyJoinMstRepository.save(existingJoin);

        /* 수량 차감 (슬롯 반환 → 다른 유저 재예약 가능) */
        applyGroupbuyCancelResult(groupbuyId, cancelQty);
    }

    private void applyGroupbuyCancelResult(Long groupbuyId, int cancelQty) {

        int updated = groupbuyAdminMapper.decreaseCurrentQty(groupbuyId, cancelQty);

        if (updated == 0) {
            log.warn("[GROUPBUY][CANCEL][FAIL] groupbuyId={} cancelQty={}",
                    groupbuyId, cancelQty);
            throw new GlobalException(ResponseCode.BAD_REQUEST , "공동구매 수량 차감 실패");
        }
    }


    private void validateGroupbuyJoin(Long groupbuyId, int joinQty) {

        GroupbuyMstEntity groupbuy = groupbuyMstRepository.findById(groupbuyId).orElseThrow(() -> new IllegalStateException("공동구매 정보 없음"));

        /* 상태 체크: 진행중(START) 또는 딜성공(SUCCESS, 픽업기간 재예약) */
        GroupbuyStatus st = groupbuy.getStatus();
        if (st != GroupbuyStatus.START && st != GroupbuyStatus.SUCCESS) {

            throw new GlobalException(ResponseCode.BAD_REQUEST , "참여 가능한 상태가 아닙니다.");
        }
        /* 기간 체크 */
        LocalDateTime now = LocalDateTime.now();
        if (st == GroupbuyStatus.START) {
            if (now.isBefore(groupbuy.getStartDate()) || now.isAfter(groupbuy.getEndDate())) {

                throw new GlobalException(ResponseCode.BAD_REQUEST , "공동구매 기간이 아닙니다.");
            }
        } else {
            /* SUCCESS: 취소로 빈 수량 재예약은 픽업 종료(PICKUP_END) 전까지 허용 */
            if (groupbuy.getPickupEndDate() == null || now.isAfter(groupbuy.getPickupEndDate())) {

                throw new GlobalException(ResponseCode.BAD_REQUEST , "재예약 가능 기간(픽업 종료)이 지났습니다.");
            }
        }
        /* 수량 초과 방지 (target_qty 한도 유지) */
        if (groupbuy.getCurrentQty() + joinQty > groupbuy.getTargetQty()) {

            throw new GlobalException(ResponseCode.BAD_REQUEST , "남은 수량을 초과했습니다.");
        }
    }

    /** 1인당 구매 제한 검증 (limitQty 0/null 이면 무제한) */
    private void validatePerPersonLimit(Long groupbuyId, int totalQty) {

        GroupbuyMstEntity groupbuy = groupbuyMstRepository.findById(groupbuyId)
                .orElseThrow(() -> new GlobalException(ResponseCode.NOT_FOUND_OBJECT, "공동구매 정보가 없습니다."));

        Integer limitQty = groupbuy.getLimitQty();
        if (limitQty != null && limitQty > 0 && totalQty > limitQty) {

            throw new GlobalException(ResponseCode.BAD_REQUEST , "1인당 최대 " + limitQty + "개까지 예약할 수 있습니다.");
        }
    }

    /**
     * 공동구매 참여 결과 반영 (수량/금액 누적)
     */
    private void applyGroupbuyJoinResult(Long groupbuyId, int joinQty) {

        /* 동시성 고려한 수량 증가 */
        int updated = groupbuyAdminMapper.increaseCurrentQty(groupbuyId, joinQty);

        if (updated == 0) {

            throw new GlobalException(ResponseCode.BAD_REQUEST , "동시 참여로 인해 공동구매 수량이 초과되었습니다.");
        }
    }

    /*****************************************************************************
     * 공동구매 예약 픽업완료 → 매출(sales_mst / sales_dtl) 처리
     *  - JOIN 상태만 처리, 완료 후 JOIN_STATUS=PAYED + SALES_ID 연결
     *  - 유효 랏(product_dtl) FIFO 배분으로 sales_dtl 생성, 부족 시 전체 롤백
     *****************************************************************************/
    @Transactional
    /** 공유하기 클릭 트래킹 적재 (userId nullable) */
    public void logGroupbuyShare(Long groupbuyId, Long storeId, Long locationId, String userId, String shareMethod) {
        java.util.Map<String,Object> p = new java.util.HashMap<>();
        p.put("groupbuyId", groupbuyId);
        p.put("storeId", storeId);
        p.put("locationId", locationId);
        p.put("userId", userId);
        p.put("shareMethod", shareMethod);
        groupbuyAdminMapper.insertGroupbuyShareLog(p);
    }

    public Long completeGroupbuyPickup(Long joinId, String adminUser) {

        GroupbuyJoinMstEntity join = groupbuyJoinMstRepository.findById(joinId)
                .orElseThrow(() -> new GlobalException(ResponseCode.NOT_FOUND_OBJECT, "예약 정보가 없습니다."));

        if (join.getJoinStatus() == GroupbuyJoinStatus.PAYED) {
            throw new GlobalException(ResponseCode.BAD_REQUEST, "이미 픽업 완료(매출 처리)된 예약입니다.");
        }
        if (join.getJoinStatus() != GroupbuyJoinStatus.JOIN) {
            throw new GlobalException(ResponseCode.BAD_REQUEST, "픽업 완료할 수 없는 상태입니다.");
        }

        GroupbuyMstEntity gb = groupbuyMstRepository.findById(join.getGroupbuyId())
                .orElseThrow(() -> new GlobalException(ResponseCode.NOT_FOUND_OBJECT, "공동구매 정보가 없습니다."));

        Long   storeId    = gb.getStoreId();
        Long   locationId = gb.getLocationId();
        Long   productId  = gb.getProductId();
        String userId     = join.getUserId();
        int    qty        = (join.getTotalQty() == null) ? 0 : join.getTotalQty();
        if (qty <= 0) {
            throw new GlobalException(ResponseCode.BAD_REQUEST, "예약 수량이 올바르지 않습니다.");
        }

        int orgPrice = groupbuyAdminMapper.selectProductOrgSalesPrice(productId);

        /* 확정 단가: 정산된 UNIT_PRICE 우선, 없으면 현재 달성 단계가, 그래도 없으면 정가 */
        int unitPrice;
        int discountRate;
        if (join.getUnitPrice() != null && join.getUnitPrice() > 0) {
            unitPrice    = join.getUnitPrice();
            discountRate = (orgPrice > 0) ? (int) Math.round((orgPrice - unitPrice) * 100.0 / orgPrice) : 0;
        } else {
            int curQty = (gb.getCurrentQty() == null) ? 0 : gb.getCurrentQty();
            java.util.Map<String,Object> step = groupbuyAdminMapper.selectGroupbuyAchievedStep(gb.getGroupbuyId(), curQty);
            if (step != null && step.get("salesPrice") != null) {
                unitPrice    = ((Number) step.get("salesPrice")).intValue();
                discountRate = (step.get("salesRate") != null) ? ((Number) step.get("salesRate")).intValue() : 0;
            } else {
                unitPrice    = orgPrice;
                discountRate = 0;
            }
        }
        if (unitPrice <= 0) unitPrice = orgPrice;

        int totalPrice    = orgPrice * qty;
        int finalPrice    = unitPrice * qty;
        int discountPrice = totalPrice - finalPrice;

        /* sales_mst */
        java.util.Map<String,Object> mst = new java.util.HashMap<>();
        mst.put("storeId",       storeId);
        mst.put("userId",        userId);
        mst.put("totalQty",      qty);
        mst.put("totalPrice",    totalPrice);
        mst.put("discountPrice", discountPrice);
        mst.put("finalPrice",    finalPrice);
        mst.put("description",   "공동구매 픽업완료 (groupbuyId=" + gb.getGroupbuyId() + ", joinId=" + joinId + ")");
        mst.put("createUser",    adminUser);
        groupbuyAdminMapper.insertGroupbuySalesMst(mst);
        Long salesId = ((Number) mst.get("salesId")).longValue();

        /* sales_dtl : 유효 랏 FIFO 배분 */
        java.util.List<java.util.Map<String,Object>> lots =
                groupbuyAdminMapper.selectGroupbuyAvailableLots(productId, locationId);

        int remaining = qty;
        int lineNo = 1;
        for (java.util.Map<String,Object> lot : lots) {
            if (remaining <= 0) break;
            Long productDtlId = ((Number) lot.get("productDtlId")).longValue();
            int  avail        = ((Number) lot.get("availableQty")).intValue();
            int  take         = Math.min(avail, remaining);
            if (take <= 0) continue;

            java.util.Map<String,Object> dtl = new java.util.HashMap<>();
            dtl.put("salesId",       salesId);
            dtl.put("productId",     productId);
            dtl.put("productDtlId",  productDtlId);
            dtl.put("lineNo",        lineNo);
            dtl.put("qty",           take);
            dtl.put("unitPrice",     orgPrice);
            dtl.put("discountPrice", (orgPrice - unitPrice) * take);
            dtl.put("discountRate",  discountRate);
            dtl.put("salesPrice",    unitPrice * take);
            dtl.put("createUser",    adminUser);
            groupbuyAdminMapper.insertGroupbuySalesDtl(dtl);
            groupbuyAdminMapper.decreaseLotStock(productDtlId, take);

            remaining -= take;
            lineNo++;
        }

        if (remaining > 0) {
            throw new GlobalException(ResponseCode.BAD_REQUEST,
                    "유효한 재고(랏)가 부족하여 매출 처리할 수 없습니다. 부족 수량: " + remaining + "개");
        }

        int updated = groupbuyAdminMapper.updateJoinPickupComplete(joinId, salesId, unitPrice, adminUser);
        if (updated == 0) {
            throw new GlobalException(ResponseCode.BAD_REQUEST, "예약 상태가 변경되어 처리할 수 없습니다.");
        }

        return salesId;
    }
}
