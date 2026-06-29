package com.jjsoft.pos.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import lombok.*;

/**
 * 유저 마스터
 */
@Entity
@Table(name = "user_mst")
@Comment("유저 마스터")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserMstEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    @Comment("내부 식별자")
    private Long id;

    @Column(name = "USER_ID", unique = true)
    @Comment("외부 연동용 유저 ID (SNS 또는 자체 ID)")
    private String userId;

    @Column(name = "SSO_ID")
    @Comment("외부 연동용 유저 ID2")
    private String ssoId;

    @Column(name = "PASSWORD", length = 100)
    @Comment("비밀번호 (SNS 연동 시 NULL 가능)")
    private String password;

    @Column(name = "NAME", length = 50)
    @Comment("이름")
    private String name;

    @Column(name = "EMAIL", length = 100, unique = true)
    @Comment("이메일")
    private String email;

    @Column(name = "PHONE", length = 20)
    @Comment("연락처")
    private String phone;

    @Column(name = "SNS_TYPE", length = 20)
    @Comment("연동 타입 (GOOGLE, NAVER, KAKAO, LOCAL)")
    private String snsType;

    @Column(name = "LAST_LOGIN_DATE")
    @Comment("마지막 로그인 일시")
    private LocalDateTime lastLoginDate;

    @Column(name = "PROFILE_IMG_URL", length = 255)
    @Comment("프로필 이미지 URL")
    private String profileImgUrl;

    @Column(name = "GENDER", length = 50)
    @Comment("성별 남/여(male/female)")
    private String gender;

    @Column(name = "AGE_RANGE", length = 50)
    @Comment("연령대")
    private String ageRange;
    
    @Column(name = "ADDRESS", length = 50)
    @Comment("수소")
    private String address;

    @Column(name = "AGE")
    @Comment("나이")
    private String age;

    @Column(name = "BIRTHDAY" , length = 50)
    @Comment("생일")
    private String birthday;

    @Column(name = "BIRTHYEAR" , length = 50)
    @Comment("출생년도")
    private String birthYear;

    @Column(name = "USE_YN", length = 1)
    @Comment("사용 여부")
    private String useYn;

    @CreationTimestamp
    @Column(name = "CREATE_DATE", updatable = false)
    @Comment("생성일자")
    private LocalDateTime createDate;

    @Column(name = "CREATE_USER", length = 50)
    @Comment("생성자")
    private String createUser;

    @UpdateTimestamp
    @Column(name = "UPDATE_DATE")
    @Comment("수정일자")
    private LocalDateTime updateDate;

    @Column(name = "UPDATE_USER", length = 50)
    @Comment("수정자")
    private String updateUser;
}
