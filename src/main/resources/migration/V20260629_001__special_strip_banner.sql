-- 특가 탭 띠배너 (2026-06-29)
-- 대상: posdb_dev (dev), 이후 prod 적용 검토
-- 노출: 모바일 특가 페이지(MobileMain) 특가명 탭 아래
-- 정책: 기간(START~END) 내 활성 배너 노출(여러 개면 캐러셀 좌우 롤링),
--       기간 외에는 기본배너(IS_DEFAULT=1) 노출

CREATE TABLE IF NOT EXISTS special_strip_banner (
  BANNER_ID    BIGINT AUTO_INCREMENT PRIMARY KEY,
  STORE_ID     BIGINT       NOT NULL                  COMMENT '점포 ID (멀티점포 대비)',
  IMAGE_URL    VARCHAR(500) NOT NULL                  COMMENT 'S3 배너 이미지 URL',
  LANDING_URL  VARCHAR(500)                           COMMENT '클릭 랜딩 URL (없으면 비클릭)',
  START_DATE   DATE                                   COMMENT '노출 시작일 (기본배너는 NULL)',
  END_DATE     DATE                                   COMMENT '노출 종료일 (포함, 기본배너는 NULL)',
  IS_DEFAULT   TINYINT      NOT NULL DEFAULT 0         COMMENT '1=기간 외 노출용 기본배너',
  IS_ACTIVE    TINYINT      NOT NULL DEFAULT 1         COMMENT '1=사용',
  SORT_ORDER   INT          NOT NULL DEFAULT 1         COMMENT '캐러셀 노출 순서(작을수록 먼저)',
  TITLE        VARCHAR(100)                            COMMENT '관리용 메모',
  CREATE_USER  VARCHAR(100)                            COMMENT '등록자',
  CREATED_AT   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UPDATED_AT   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  KEY idx_store_period (STORE_ID, IS_ACTIVE, START_DATE, END_DATE),
  KEY idx_store_default (STORE_ID, IS_DEFAULT)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='특가 탭 띠배너';
