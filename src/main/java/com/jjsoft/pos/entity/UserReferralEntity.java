package com.jjsoft.pos.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 추천인 정보 Entity
 *
 * 신규 가입자가 추천인을 입력했을 때 저장되는 테이블
 * USER_ID 는 가입한 사용자
 * REFERRER_PHONE 은 추천인 전화번호
 */
@Entity
@Table(name = "user_referral_mst")
@Comment("추천인 정보 Entity")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserReferralEntity {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "REFERRAL_ID")
    @Comment("추천 ID")
    private Long referralId;

    /**
     * 가입한 사용자
     */
	@Column(name = "USER_ID")
    @Comment("가입한 유저 ID")
    private String userId;

	@Column(name = "USER_NM")
	@Comment("가입한 유저 이름")
	private String userNm;

    /**
     * 추천인 USER_ID (회원일 경우)
     */
    @Column(name = "REFERRER_USER_ID")
    @Comment("추천인 USER_ID")
    private String referrerUserId;
    
    @Column(name = "REFERRER_USER_NM")
    @Comment("추천인 유저 이름")
    private String referrerUserNm;

    /**
     * 추천인 전화번호
     */
    @Column(name = "REFERRER_PHONE")
    @Comment("추천인 전화번호")
    private String referrerPhone;

    /**
     * 추천인 없음 여부
     */
    @Column(name = "NONE_YN")
    @Comment("추천인 없음 여부")
    private String noneYn;

    /**
     * 생성일
     */
    @CreationTimestamp
    @Column(name = "CREATE_DATE")
    @Comment("생성일자")
    private LocalDateTime createDate;

    /**
     * 생성자
     */
    @Column(name = "CREATE_USER")
    @Comment("생성자")
    private String createUser;
    
    @UpdateTimestamp
    @Column(name = "UPDATE_DATE")
    @Comment("수정일자")
    private LocalDateTime updateDate;

    @Column(name = "UPDATE_USER")
    @Comment("수정자")
    private String updateUser;

}
