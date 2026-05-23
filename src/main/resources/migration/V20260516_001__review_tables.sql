-- 상품 리뷰 기능 (2026-05-16)
-- 대상: posdb_dev (dev), 이후 prod 적용 검토
-- 작성 정책: 1상품-1유저-1리뷰 (UNIQUE(PRODUCT_ID, USER_ID)), 수정/삭제 가능
-- 작성 권한: Keycloak 인증 유저
-- 노출: 모바일 상품 상세 + 어드민 모니터링

CREATE TABLE IF NOT EXISTS review_mst (
  REVIEW_ID       BIGINT AUTO_INCREMENT PRIMARY KEY,
  PRODUCT_ID      BIGINT       NOT NULL                   COMMENT '상품 ID (product_mst FK 의도)',
  USER_ID         VARCHAR(255) NOT NULL                   COMMENT 'Keycloak sub (UUID)',
  USER_NICKNAME   VARCHAR(100)                            COMMENT 'preferred_username 캐시 (목록 표시용)',
  RATING          TINYINT      NOT NULL                   COMMENT '별점 1~5',
  CONTENT         TEXT         NOT NULL                   COMMENT '리뷰 본문',
  HELPFUL_COUNT   INT          NOT NULL DEFAULT 0         COMMENT '도움됐어요 집계 캐시',
  STATUS          VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE'  COMMENT 'ACTIVE / HIDDEN / DELETED',
  CREATED_AT      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UPDATED_AT      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_product_user (PRODUCT_ID, USER_ID),
  KEY idx_product_status (PRODUCT_ID, STATUS),
  KEY idx_user (USER_ID),
  KEY idx_created_at (CREATED_AT)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='상품 리뷰';

CREATE TABLE IF NOT EXISTS review_reaction (
  REACTION_ID     BIGINT AUTO_INCREMENT PRIMARY KEY,
  REVIEW_ID       BIGINT       NOT NULL,
  USER_ID         VARCHAR(255) NOT NULL                   COMMENT 'Keycloak sub',
  REACTION_TYPE   VARCHAR(20)  NOT NULL DEFAULT 'HELPFUL' COMMENT '확장 여지 (LIKE 등)',
  CREATED_AT      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uk_review_user_type (REVIEW_ID, USER_ID, REACTION_TYPE),
  KEY idx_review (REVIEW_ID),
  CONSTRAINT fk_reaction_review FOREIGN KEY (REVIEW_ID) REFERENCES review_mst(REVIEW_ID) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='리뷰 도움됐어요/좋아요 원본';
