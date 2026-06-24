-- 멀티점포 점포 격리/권한 (2026-06-24)
-- 대상: posdb_dev (dev) 먼저 적용·검증 후 prod 검토
-- 적용 방식: 본 프로젝트는 Flyway 미사용(ddl-auto: none) → DB 관리자 승인 하에 수동 적용
-- 정책:
--   * user_store_map = 유저↔점포 접근범위(점포별 역할 STORE_ADMIN/STAFF)
--   * 전역 등급(SUPER_ADMIN 등)은 Keycloak realm role 로 관리 (본 테이블 ROLE 은 점포 내 역할)
--   * 시크릿(발신키/PW)은 평문 저장 금지 → store_notify_config 는 환경변수 키 '이름'만 보관
-- 주의: 실제 키/비밀번호/PII 값은 본 파일에 기재하지 않는다.

-- 1) 유저-점포 매핑에 점포 내 역할/사용여부 추가
ALTER TABLE user_store_map
  ADD COLUMN ROLE    VARCHAR(20) NULL COMMENT '점포 내 역할 (STORE_ADMIN / STAFF)' AFTER STORE_ID,
  ADD COLUMN USE_YN  VARCHAR(1)  NOT NULL DEFAULT 'Y' COMMENT '사용 여부';

-- 2) 감사 로그에 점포 컨텍스트 귀속
ALTER TABLE audit_log
  ADD COLUMN STORE_ID BIGINT NULL COMMENT '요청 점포 ID (StoreContext 기준)' AFTER URL;

CREATE INDEX idx_audit_store_date ON audit_log (STORE_ID, CREATE_DATE);

-- 3) 점포별 알림(비즈톡) 발신 설정 — 시크릿은 '환경변수 키 이름'만 저장 (평문 금지)
CREATE TABLE IF NOT EXISTS store_notify_config (
  STORE_ID          BIGINT       NOT NULL                COMMENT '점포 ID (store_mst FK 의도)',
  BSID              VARCHAR(50)                          COMMENT '비즈톡 발신 프로필 ID (비밀 아님)',
  SENDER_KEY_ENV    VARCHAR(100)                         COMMENT '발신키를 담은 환경변수 키 이름 (값 아님)',
  PASSWD_ENV        VARCHAR(100)                         COMMENT '발신 PW를 담은 환경변수 키 이름 (값 아님)',
  TEMPLATE_PREFIX   VARCHAR(50)                          COMMENT '점포별 템플릿 코드 프리픽스 (선택)',
  USE_YN            VARCHAR(1)   NOT NULL DEFAULT 'Y'    COMMENT '사용 여부',
  CREATE_DATE       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UPDATE_DATE       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (STORE_ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='점포별 알림 발신 설정 (시크릿은 ENV 키 이름만)';

-- 4) 거래성 조회 가속용 점포 복합 인덱스 점검(존재 시 무시) — 필요 시 점진 추가
-- CREATE INDEX idx_product_store ON product_mst (STORE_ID);
-- CREATE INDEX idx_sales_store_date ON sales_mst (STORE_ID, ...);

-- 롤백 참고:
-- ALTER TABLE user_store_map DROP COLUMN ROLE, DROP COLUMN USE_YN;
-- ALTER TABLE audit_log DROP COLUMN STORE_ID; DROP INDEX idx_audit_store_date ON audit_log;
-- DROP TABLE store_notify_config;
