package com.jjsoft.pos.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jjsoft.pos.entity.UserMstEntity;
import com.jjsoft.pos.repository.UserMstRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 * 회원 탈퇴 처리.
 *
 * 정책: 탈퇴 회원의 개인정보는 어디에 있든 모두 제거하되, 활동·구매 데이터의 행 자체는 보존한다.
 *  - user_mst : 개인정보 컬럼 전부 NULL, USER_ID 를 복원 불가능한 해시 가명(WITHDRAWN_xxxx)으로 변환, USE_YN='N'.
 *  - 활동/구매 테이블 : 행은 그대로 두고 USER_ID 를 동일 가명으로 변환(탈퇴 식별 가능),
 *                      추가로 각 테이블에 박혀 있는 이 회원의 PII 컬럼(전화/이름/닉네임/메시지 등)도 NULL 처리.
 *
 * user_mst.USER_ID 는 sales_mst/special_rsv_mst/user_referral_mst/return_mst 등이 FK(RESTRICT)로
 * 참조하므로 행 자체를 삭제할 수 없다. 부모·자식 USER_ID 를 한 번에 가명으로 치환하기 위해
 * 트랜잭션 동안만 FOREIGN_KEY_CHECKS 를 끈다(치환 후 부모·자식이 동일 가명을 가지므로 정합성 유지).
 *
 * 존재하지 않는 테이블/컬럼은 information_schema 로 확인 후 스킵한다.
 */
@Service
@RequiredArgsConstructor
@Log4j2
public class UserWithdrawalService {

    private final JdbcTemplate jdbc;
    private final UserMstRepository userMstRepository;

    @Value("${app.withdraw.salt:jjpos-withdraw-2nd}")
    private String salt;

    /** USER_ID(이메일/preferred_username) 로 회원 활동 데이터를 참조하는 테이블들 (행 보존, USER_ID만 가명 치환) */
    private static final String[][] USERID_TABLES = {
        {"sales_mst", "USER_ID"},
        {"special_rsv_mst", "USER_ID"},
        {"return_mst", "USER_ID"},
        {"groupbuy_join_mst", "USER_ID"},
        {"biztalk_send_log", "USER_ID"},
        {"audit_log", "USER_ID"},
        {"user_referral_mst", "USER_ID"},
    };

    /** USER_ID 가 이 회원(가명)인 행에서 추가로 NULL 처리할 PII 컬럼. {테이블, 컬럼...} */
    private static final String[][] PII_NULL_COLUMNS = {
        {"special_rsv_mst", "PHONE_NO"},
        {"biztalk_send_log", "PHONE_NO", "MESSAGE_CONTENT"},
        {"audit_log", "USER_NAME"},
        {"user_referral_mst", "USER_NM"},
        {"review_mst", "USER_NICKNAME"},
    };

    /**
     * 회원 탈퇴: 모든 개인정보 제거 + USER_ID 가명화(활동 데이터 행 보존).
     * @param loginId      preferred_username (user_mst 조회 키)
     * @param keycloakSub  Keycloak sub (리뷰 테이블 키)
     */
    @Transactional
    public void withdrawMember(String loginId, String keycloakSub) {
        UserMstEntity u = userMstRepository.getUserByUserId(loginId).orElse(null);
        if (u == null) {
            log.warn("탈퇴 대상 회원 없음: {}", loginId);
            return;
        }

        // 활동 데이터가 참조할 수 있는 이 회원의 기존 식별자(로그인ID/이메일) 수집
        Set<String> keys = new LinkedHashSet<>();
        if (u.getUserId() != null) keys.add(u.getUserId());
        if (u.getEmail()  != null) keys.add(u.getEmail());
        if (loginId       != null) keys.add(loginId);

        // 복원 불가능한 가명. 탈퇴 식별이 가능하도록 WITHDRAWN_ 접두.
        // 불변 내부 id 기반으로 생성 → 재가입 후 재탈퇴해도 가명이 겹치지 않음(user_mst.USER_ID UNIQUE 충돌 방지).
        String pseudo = "WITHDRAWN_" + sha256(salt + ":" + u.getId());

        // FK(RESTRICT) 회피: 부모(user_mst)·자식 USER_ID 를 동일 가명으로 동시 치환
        jdbc.execute("SET FOREIGN_KEY_CHECKS=0");
        try {
            // 1) 활동/구매 테이블 USER_ID 가명 치환 (행 보존)
            for (String[] t : USERID_TABLES) {
                updateUserId(t[0], t[1], keys, pseudo);
            }

            // 2) 리뷰는 Keycloak sub(UUID) 로 키잉됨 → sub 로 USER_ID 가명 치환
            if (keycloakSub != null && !keycloakSub.isBlank()) {
                if (tableExists("review_reaction")) {
                    jdbc.update("UPDATE review_reaction SET USER_ID = ? WHERE USER_ID = ?", pseudo, keycloakSub);
                }
                if (tableExists("review_mst")) {
                    jdbc.update("UPDATE review_mst SET USER_ID = ? WHERE USER_ID = ?", pseudo, keycloakSub);
                }
            }

            // 3) 활동 테이블에 박힌 이 회원의 PII 컬럼 NULL 처리 (USER_ID가 이미 가명으로 치환된 행 대상)
            for (String[] tc : PII_NULL_COLUMNS) {
                nullPiiColumns(tc, pseudo);
            }

            // 4) 다른 회원의 가입 행에 '추천인'으로 박힌 이 회원의 PII 제거 + 추천인ID 가명화
            if (tableExists("user_referral_mst") && columnExists("user_referral_mst", "REFERRER_USER_ID")) {
                clearReferrer(keys, pseudo);
            }

            // 5) user_mst: 개인정보 전부 제거 + USER_ID 가명화 + 사용여부 N
            jdbc.update(
                "UPDATE user_mst SET USER_ID = ?, SSO_ID = NULL, PASSWORD = NULL, NAME = NULL, "
              + "EMAIL = NULL, PHONE = NULL, PROFILE_IMG_URL = NULL, GENDER = NULL, AGE_RANGE = NULL, "
              + "ADDRESS = NULL, AGE = NULL, BIRTHDAY = NULL, USE_YN = 'N' WHERE ID = ?",
                pseudo, u.getId());
        } finally {
            jdbc.execute("SET FOREIGN_KEY_CHECKS=1");
        }

        log.info("회원 탈퇴 처리 완료(개인정보 제거+가명화): id={}, pseudo={}, 매칭키수={}", u.getId(), pseudo, keys.size());
    }

    private void updateUserId(String table, String col, Set<String> keys, String pseudo) {
        if (keys.isEmpty() || !tableExists(table)) return;
        List<Object> args = new ArrayList<>();
        args.add(pseudo);
        String in = placeholders(keys, args);
        String sql = "UPDATE " + table + " SET " + col + " = ? WHERE " + col + " IN (" + in + ")";
        int n = jdbc.update(sql, args.toArray());
        if (n > 0) log.info("가명화 {}: {}건", table, n);
    }

    /**
     * tc = {table, piiCol1, piiCol2...} : USER_ID = pseudo 인 행의 PII 컬럼들을 제거.
     * NULL 허용 컬럼은 NULL 로, NOT NULL 컬럼은 빈 문자열('')로 마스킹한다.
     */
    private void nullPiiColumns(String[] tc, String pseudo) {
        String table = tc[0];
        if (!tableExists(table)) return;
        StringBuilder set = new StringBuilder();
        for (int i = 1; i < tc.length; i++) {
            if (!columnExists(table, tc[i])) continue;
            if (set.length() > 0) set.append(", ");
            set.append(tc[i]).append(columnNullable(table, tc[i]) ? " = NULL" : " = ''");
        }
        if (set.length() == 0) return;
        int n = jdbc.update("UPDATE " + table + " SET " + set + " WHERE USER_ID = ?", pseudo);
        if (n > 0) log.info("PII 제거 {}: {}건", table, n);
    }

    private void clearReferrer(Set<String> keys, String pseudo) {
        List<Object> args = new ArrayList<>();
        args.add(pseudo);
        StringBuilder set = new StringBuilder("REFERRER_USER_ID = ?");
        if (columnExists("user_referral_mst", "REFERRER_PHONE"))
            set.append(columnNullable("user_referral_mst", "REFERRER_PHONE") ? ", REFERRER_PHONE = NULL" : ", REFERRER_PHONE = ''");
        if (columnExists("user_referral_mst", "REFERRER_USER_NM"))
            set.append(columnNullable("user_referral_mst", "REFERRER_USER_NM") ? ", REFERRER_USER_NM = NULL" : ", REFERRER_USER_NM = ''");
        String in = placeholders(keys, args);
        String sql = "UPDATE user_referral_mst SET " + set + " WHERE REFERRER_USER_ID IN (" + in + ")";
        int n = jdbc.update(sql, args.toArray());
        if (n > 0) log.info("추천인 PII 제거 user_referral_mst: {}건", n);
    }

    /** keys 를 "?,?,..." 로 만들고 args 에 값을 채운다 */
    private String placeholders(Set<String> keys, List<Object> args) {
        StringBuilder in = new StringBuilder();
        for (String k : keys) {
            if (in.length() > 0) in.append(",");
            in.append("?");
            args.add(k);
        }
        return in.toString();
    }

    private boolean tableExists(String table) {
        Integer c = jdbc.queryForObject(
            "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = ?",
            Integer.class, table);
        return c != null && c > 0;
    }

    private boolean columnExists(String table, String column) {
        Integer c = jdbc.queryForObject(
            "SELECT COUNT(*) FROM information_schema.columns WHERE table_schema = DATABASE() AND table_name = ? AND column_name = ?",
            Integer.class, table, column);
        return c != null && c > 0;
    }

    /** 컬럼이 NULL 허용인지 (NOT NULL 컬럼은 NULL 대신 ''로 마스킹해야 함) */
    private boolean columnNullable(String table, String column) {
        String yn = jdbc.queryForObject(
            "SELECT IS_NULLABLE FROM information_schema.columns WHERE table_schema = DATABASE() AND table_name = ? AND column_name = ?",
            String.class, table, column);
        return "YES".equalsIgnoreCase(yn);
    }

    private static String sha256(String s) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] d = md.digest(s.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(d.length * 2);
            for (byte b : d) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            throw new IllegalStateException("가명 생성 실패", e);
        }
    }
}
