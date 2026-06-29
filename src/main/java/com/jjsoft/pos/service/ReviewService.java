package com.jjsoft.pos.service;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jjsoft.pos.dto.review.ReviewRequestDto;
import com.jjsoft.pos.dto.review.ReviewResponseDto;
import com.jjsoft.pos.dto.review.ReviewSummaryDto;
import com.jjsoft.pos.entity.ProductMstEntity;
import com.jjsoft.pos.entity.ReviewMstEntity;
import com.jjsoft.pos.entity.ReviewReactionEntity;
import com.jjsoft.pos.entity.UserMstEntity;
import com.jjsoft.pos.enums.ResponseCode;
import com.jjsoft.pos.exception.GlobalException;
import com.jjsoft.pos.repository.KeycloakFederatedIdentityRepository;
import com.jjsoft.pos.repository.ProductMstRepository;
import com.jjsoft.pos.repository.ReviewMstRepository;
import com.jjsoft.pos.repository.ReviewReactionRepository;
import com.jjsoft.pos.repository.UserMstRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@RequiredArgsConstructor
@Log4j2
public class ReviewService {

    private static final String STATUS_ACTIVE = "ACTIVE";
    private static final String STATUS_HIDDEN = "HIDDEN";
    private static final String STATUS_DELETED = "DELETED";
    private static final String REACTION_HELPFUL = "HELPFUL";

    private final ReviewMstRepository reviewRepo;
    private final ReviewReactionRepository reactionRepo;
    private final ProductMstRepository productMstRepository;
    private final UserMstRepository userMstRepository;
    private final KeycloakFederatedIdentityRepository keycloakFederatedIdentityRepository;
    private final com.jjsoft.pos.repository.ReviewEventCheckRepository reviewEventCheckRepository;

    @Transactional(readOnly = true)
    public Page<ReviewResponseDto> listByProduct(Long productId, String callerUserId, Pageable pageable) {
        Page<ReviewMstEntity> page = reviewRepo.findByProductIdAndStatus(productId, STATUS_ACTIVE, pageable);
        Map<Long, Boolean> helpfulMap = helpfulMapFor(page.getContent(), callerUserId);
        return page.map(e -> ReviewResponseDto.from(e, callerUserId, helpfulMap.getOrDefault(e.getReviewId(), false)));
    }

    @Transactional(readOnly = true)
    public ReviewSummaryDto summary(Long productId) {
        long total = reviewRepo.countByProductIdAndStatus(productId, STATUS_ACTIVE);
        Double avg = reviewRepo.averageRatingByProductId(productId);
        Map<Integer, Long> dist = new LinkedHashMap<>();
        for (int i = 5; i >= 1; i--) dist.put(i, 0L);
        for (Object[] row : reviewRepo.ratingDistribution(productId)) {
            Integer rating = ((Number) row[0]).intValue();
            Long cnt = ((Number) row[1]).longValue();
            dist.put(rating, cnt);
        }
        return ReviewSummaryDto.builder()
                .productId(productId)
                .totalCount(total)
                .averageRating(avg == null ? 0.0 : Math.round(avg * 10) / 10.0)
                .ratingDistribution(dist)
                .build();
    }

    /** 여러 상품의 요약을 한 번에. 상품 목록 카드에서 사용. */
    @Transactional(readOnly = true)
    public Map<Long, ReviewSummaryDto> summaries(List<Long> productIds) {
        if (productIds == null || productIds.isEmpty()) return Collections.emptyMap();
        Map<Long, ReviewSummaryDto> result = new LinkedHashMap<>();
        for (Long productId : productIds) {
            if (productId == null) continue;
            result.put(productId, summary(productId));
        }
        return result;
    }

    /** myReviewCount: active review count for a user (mypage) */
    @Transactional(readOnly = true)
    public long myReviewCount(String userId) {
        if (userId == null || userId.isBlank()) return 0L;
        return reviewRepo.countByUserIdAndStatus(userId, STATUS_ACTIVE);
    }

    /** myReviews: active reviews of a user with product name (mypage popup) */
    @Transactional(readOnly = true)
    public List<ReviewResponseDto> myReviews(String userId) {
        if (userId == null || userId.isBlank()) return Collections.emptyList();
        List<ReviewMstEntity> list = reviewRepo.findByUserIdAndStatusOrderByCreatedAtDesc(userId, STATUS_ACTIVE);
        List<Long> productIds = list.stream().map(ReviewMstEntity::getProductId)
                .filter(Objects::nonNull).distinct().collect(Collectors.toList());
        Map<Long, String> productNmMap = productIds.isEmpty() ? Collections.emptyMap()
                : productMstRepository.findAllById(productIds).stream()
                    .collect(Collectors.toMap(ProductMstEntity::getProductId, ProductMstEntity::getProductNm, (a, b) -> a));
        return list.stream().map(e -> {
            ReviewResponseDto dto = ReviewResponseDto.from(e, userId, false);
            dto.setProductNm(productNmMap.get(e.getProductId()));
            return dto;
        }).collect(Collectors.toList());
    }

    @Transactional
    public ReviewResponseDto create(ReviewRequestDto req, String userId, String userNickname) {
        validateRating(req.getRating());
        validateContent(req.getContent());
        if (req.getProductId() == null) throw new GlobalException(ResponseCode.BAD_REQUEST, "productId is required");

        reviewRepo.findByProductIdAndUserId(req.getProductId(), userId).ifPresent(r -> {
            throw new GlobalException(ResponseCode.BAD_REQUEST, "이미 작성한 리뷰가 있습니다. 수정해주세요.");
        });

        ReviewMstEntity saved = reviewRepo.save(ReviewMstEntity.builder()
                .productId(req.getProductId())
                .userId(userId)
                .userNickname(userNickname)
                .rating(req.getRating())
                .content(req.getContent().trim())
                .helpfulCount(0)
                .status(STATUS_ACTIVE)
                .build());
        log.info("review.create reviewId={} productId={} userId={}", saved.getReviewId(), saved.getProductId(), userId);
        return ReviewResponseDto.from(saved, userId, false);
    }

    @Transactional
    public ReviewResponseDto update(Long reviewId, ReviewRequestDto req, String userId) {
        ReviewMstEntity e = reviewRepo.findById(reviewId)
                .orElseThrow(() -> new GlobalException(ResponseCode.NOT_FOUND, "리뷰가 없습니다"));
        if (!STATUS_ACTIVE.equals(e.getStatus())) {
            throw new GlobalException(ResponseCode.BAD_REQUEST, "수정할 수 없는 상태입니다");
        }
        if (!e.getUserId().equals(userId)) {
            throw new GlobalException(ResponseCode.UNAUTHORIZED, "본인 리뷰만 수정 가능합니다");
        }
        if (req.getRating() != null) {
            validateRating(req.getRating());
            e.setRating(req.getRating());
        }
        if (req.getContent() != null) {
            validateContent(req.getContent());
            e.setContent(req.getContent().trim());
        }
        boolean helpful = reactionRepo.findByReviewIdAndUserIdAndReactionType(reviewId, userId, REACTION_HELPFUL).isPresent();
        return ReviewResponseDto.from(e, userId, helpful);
    }

    @Transactional
    public void delete(Long reviewId, String userId) {
        ReviewMstEntity e = reviewRepo.findById(reviewId)
                .orElseThrow(() -> new GlobalException(ResponseCode.NOT_FOUND, "리뷰가 없습니다"));
        if (!e.getUserId().equals(userId)) {
            throw new GlobalException(ResponseCode.UNAUTHORIZED, "본인 리뷰만 삭제 가능합니다");
        }
        e.setStatus(STATUS_DELETED);
        log.info("review.delete reviewId={} userId={}", reviewId, userId);
    }

    @Transactional
    public ReviewResponseDto toggleHelpful(Long reviewId, String userId) {
        ReviewMstEntity e = reviewRepo.findById(reviewId)
                .orElseThrow(() -> new GlobalException(ResponseCode.NOT_FOUND, "리뷰가 없습니다"));
        if (!STATUS_ACTIVE.equals(e.getStatus())) {
            throw new GlobalException(ResponseCode.BAD_REQUEST, "비활성 리뷰입니다");
        }
        if (e.getUserId().equals(userId)) {
            throw new GlobalException(ResponseCode.BAD_REQUEST, "본인 리뷰에는 반응할 수 없습니다");
        }
        Optional<ReviewReactionEntity> existing = reactionRepo.findByReviewIdAndUserIdAndReactionType(reviewId, userId, REACTION_HELPFUL);
        boolean nowHelpful;
        if (existing.isPresent()) {
            reactionRepo.delete(existing.get());
            e.setHelpfulCount(Math.max(0, e.getHelpfulCount() - 1));
            nowHelpful = false;
        } else {
            reactionRepo.save(ReviewReactionEntity.builder()
                    .reviewId(reviewId)
                    .userId(userId)
                    .reactionType(REACTION_HELPFUL)
                    .build());
            e.setHelpfulCount(e.getHelpfulCount() + 1);
            nowHelpful = true;
        }
        return ReviewResponseDto.from(e, userId, nowHelpful);
    }

    // ── admin ───────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public Page<ReviewResponseDto> adminList(String status, Long productId, String phoneLast4, Pageable pageable) {
        Page<ReviewMstEntity> page;
        if (phoneLast4 != null && !phoneLast4.isBlank()) {
            List<String> usernames = userMstRepository.findByPhoneEndingWith(phoneLast4.trim()).stream()
                    .map(UserMstEntity::getUserId).filter(Objects::nonNull).distinct().collect(Collectors.toList());
            List<String> subs = usernames.isEmpty() ? Collections.emptyList()
                    : keycloakFederatedIdentityRepository.findSubsByUsernames(usernames);
            if (subs.isEmpty()) {
                page = Page.empty(pageable);
            } else if (status != null && !status.isBlank()) {
                page = reviewRepo.findByUserIdInAndStatus(subs, status, pageable);
            } else {
                page = reviewRepo.findByUserIdIn(subs, pageable);
            }
        } else if (productId != null && status != null) {
            page = reviewRepo.findByProductIdAndStatus(productId, status, pageable);
        } else {
            page = reviewRepo.findAll(pageable);
        }

        // 페이지에 등장한 productId들로 product_mst 한 번에 조회 → productNm 매핑
        List<Long> productIds = page.getContent().stream()
                .map(ReviewMstEntity::getProductId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
        Map<Long, String> productNmMap = productIds.isEmpty()
                ? Collections.emptyMap()
                : productMstRepository.findAllById(productIds).stream()
                    .collect(Collectors.toMap(ProductMstEntity::getProductId, ProductMstEntity::getProductNm));

        // 리뷰 userId는 키클락 sub(UUID)인데 user_mst.userId는 preferred_username 이므로 직접 조인 불가.
        // Keycloak USER_ENTITY(ID=sub → USERNAME=preferred_username)로 브리지한 뒤 user_mst를 조회한다.
        List<String> subs = page.getContent().stream()
                .map(ReviewMstEntity::getUserId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
        Map<String, String> subToUsername = subs.isEmpty()
                ? Collections.emptyMap()
                : keycloakFederatedIdentityRepository.findUsernamesBySub(subs);
        List<String> usernames = subToUsername.values().stream()
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
        Map<String, UserMstEntity> userByUsername = usernames.isEmpty()
                ? Collections.emptyMap()
                : userMstRepository.findByUserIdIn(usernames).stream()
                    .collect(Collectors.toMap(UserMstEntity::getUserId, u -> u, (a, b) -> a));

        // 유저별 이벤트 참여 체크 (sub 단위) — 동일 유저의 모든 리뷰 행에 동일 반영.
        // 테이블 미생성 등으로 조회 실패해도 리뷰 목록 자체는 정상 동작하도록 방어.
        Set<String> participatedTmp = Collections.emptySet();
        if (!subs.isEmpty()) {
            try {
                participatedTmp = new HashSet<>(reviewEventCheckRepository.findParticipatedUserIds(subs));
            } catch (Exception ex) {
                log.warn("review_event_check 조회 실패(테이블 미생성 가능) - 이벤트참여 체크 생략: {}", ex.getMessage());
            }
        }
        final Set<String> participatedSubs = participatedTmp;

        return page.map(e -> {
            ReviewResponseDto dto = ReviewResponseDto.from(e, null, false);
            dto.setProductNm(productNmMap.get(e.getProductId()));
            dto.setEventParticipated(participatedSubs.contains(e.getUserId()));
            String username = subToUsername.get(e.getUserId());
            UserMstEntity user = username == null ? null : userByUsername.get(username);
            if (user != null) {
                dto.setUserPhoneLast4(phoneLast4(user.getPhone()));
                dto.setKakaoAccount("KAKAO".equalsIgnoreCase(user.getSnsType()) ? user.getEmail() : null);
                // 작성자 표시명이 비었거나 마스킹(*)뿐이면 user_mst 실명을 '마스킹해서' 보완 (PII 비노출)
                String nick = dto.getUserNickname();
                if ((nick == null || nick.replace("*", "").trim().isEmpty())
                        && user.getName() != null && !user.getName().isBlank()) {
                    dto.setUserNickname(com.jjsoft.pos.util.NicknameMaskUtil.mask(user.getName()));
                }
            }
            return dto;
        });
    }

    /** 전화번호에서 숫자만 추려 뒤 4자리 반환 (없으면 null) */
    private String phoneLast4(String phone) {
        if (phone == null) return null;
        String digits = phone.replaceAll("\\D", "");
        if (digits.length() < 4) return null;
        return digits.substring(digits.length() - 4);
    }

    /** 유저(sub) 단위 이벤트 참여 체크 upsert — 동일 유저의 모든 리뷰 행에 반영되며 영구 저장된다. */
    @Transactional
    public void setEventParticipation(String userId, boolean participated, String adminUser) {
        if (userId == null || userId.isBlank()) {
            throw new GlobalException(ResponseCode.BAD_REQUEST, "userId가 필요합니다.");
        }
        com.jjsoft.pos.entity.ReviewEventCheckEntity entity = reviewEventCheckRepository.findById(userId)
                .orElseGet(() -> com.jjsoft.pos.entity.ReviewEventCheckEntity.builder().userId(userId).build());
        entity.setParticipated(participated);
        entity.setUpdateUser(adminUser);
        reviewEventCheckRepository.save(entity);
    }

    @Transactional
    public ReviewResponseDto adminChangeStatus(Long reviewId, String newStatus) {
        if (!STATUS_ACTIVE.equals(newStatus) && !STATUS_HIDDEN.equals(newStatus) && !STATUS_DELETED.equals(newStatus)) {
            throw new GlobalException(ResponseCode.BAD_REQUEST, "허용되지 않는 status");
        }
        ReviewMstEntity e = reviewRepo.findById(reviewId)
                .orElseThrow(() -> new GlobalException(ResponseCode.NOT_FOUND, "리뷰가 없습니다"));
        e.setStatus(newStatus);
        log.info("review.admin.status reviewId={} -> {}", reviewId, newStatus);
        return ReviewResponseDto.from(e, null, false);
    }

    // ── helpers ─────────────────────────────────────────────────────────────

    private void validateRating(Integer rating) {
        if (rating == null || rating < 1 || rating > 5) {
            throw new GlobalException(ResponseCode.BAD_REQUEST, "별점은 1~5 사이여야 합니다");
        }
    }

    private void validateContent(String content) {
        if (content == null || content.trim().isEmpty()) {
            throw new GlobalException(ResponseCode.BAD_REQUEST, "내용을 입력해주세요");
        }
        if (content.length() > 2000) {
            throw new GlobalException(ResponseCode.BAD_REQUEST, "내용은 최대 2000자입니다");
        }
    }

    private Map<Long, Boolean> helpfulMapFor(List<ReviewMstEntity> reviews, String callerUserId) {
        if (callerUserId == null || reviews.isEmpty()) return Collections.emptyMap();
        List<Long> ids = reviews.stream().map(ReviewMstEntity::getReviewId).collect(Collectors.toList());
        return reactionRepo.findByReviewIdInAndUserIdAndReactionType(ids, callerUserId, REACTION_HELPFUL).stream()
                .collect(Collectors.toMap(ReviewReactionEntity::getReviewId, r -> true));
    }
}
