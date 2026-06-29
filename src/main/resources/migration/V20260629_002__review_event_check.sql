-- 리뷰 관리: 유저별 이벤트 참여 체크 (2026-06-29)
-- 대상: posdb_dev(dev) → 이후 prod 적용 검토
-- 관리자가 리뷰 작성 유저의 이벤트 참여 여부를 체크/기록. 유저(Keycloak sub) 단위로 1건.
-- 동일 유저가 여러 리뷰(여러 행)여도 한 건으로 관리 → 전화번호 재검색해도 체크가 유지됨.

CREATE TABLE IF NOT EXISTS review_event_check (
  USER_ID       VARCHAR(255) NOT NULL PRIMARY KEY            COMMENT '리뷰 작성자 Keycloak sub',
  PARTICIPATED  TINYINT      NOT NULL DEFAULT 0              COMMENT '1=이벤트 참여 체크',
  UPDATE_USER   VARCHAR(100)                                 COMMENT '체크한 관리자',
  CREATED_AT    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UPDATED_AT    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  KEY idx_participated (PARTICIPATED)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='리뷰 유저별 이벤트 참여 체크';
