package com.jjsoft.pos.dto.review;

import java.time.LocalDateTime;

import com.jjsoft.pos.entity.ReviewMstEntity;
import com.jjsoft.pos.util.NicknameMaskUtil;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReviewResponseDto {
    private Long reviewId;
    private Long productId;
    private String productNm;          // 어드민 목록 표시용 (모바일 응답에서는 null)
    private String userId;
    private String userNickname;       // 마스킹된 표시명 (본인 리뷰는 원본)
    private String userPhoneLast4;     // 어드민 전용 — 전화번호 뒤 4자리 (모바일 응답에서는 null)
    private String kakaoAccount;       // 어드민 전용 — 카카오 계정(이메일) (모바일 응답에서는 null)
    private boolean eventParticipated; // 어드민 전용 — 유저별 이벤트 참여 체크 여부
    private Integer rating;
    private String content;
    private Integer helpfulCount;
    private String status;
    private boolean ownedByCaller;     // 호출 유저가 작성자인지
    private boolean helpfulByCaller;   // 호출 유저가 도움됐어요 했는지
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ReviewResponseDto from(ReviewMstEntity e, String callerUserId, boolean helpfulByCaller) {
        // 작성자명은 항상 마스킹 (본인 리뷰도 동일). ownedByCaller는 수정/삭제 버튼 노출 용도만.
        boolean owned = callerUserId != null && callerUserId.equals(e.getUserId());
        String displayName = NicknameMaskUtil.mask(e.getUserNickname());
        return ReviewResponseDto.builder()
                .reviewId(e.getReviewId())
                .productId(e.getProductId())
                .userId(e.getUserId())
                .userNickname(displayName)
                .rating(e.getRating())
                .content(e.getContent())
                .helpfulCount(e.getHelpfulCount())
                .status(e.getStatus())
                .ownedByCaller(owned)
                .helpfulByCaller(helpfulByCaller)
                .createdAt(e.getCreatedAt())
                .updatedAt(e.getUpdatedAt())
                .build();
    }
}
