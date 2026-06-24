package com.jjsoft.pos.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import lombok.*;

/**
 * 점포별 알림(비즈톡) 발신 설정.
 *
 * <p>보안: 발신키/PW 같은 시크릿 "값"은 저장하지 않는다.
 * 값을 담은 환경변수의 "키 이름"({@code senderKeyEnv}, {@code passwdEnv})만 보관하고,
 * 실제 값은 런타임에 환경변수에서 읽는다(평문 저장 금지 — 글로벌 지침 5).</p>
 */
@Entity
@Table(name = "store_notify_config")
@Comment("점포별 알림 발신 설정 (시크릿은 ENV 키 이름만)")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class StoreNotifyConfigEntity {

    @Id
    @Column(name = "STORE_ID")
    @Comment("점포 ID")
    private Long storeId;

    @Column(name = "BSID", length = 50)
    @Comment("비즈톡 발신 프로필 ID (비밀 아님)")
    private String bsid;

    @Column(name = "SENDER_KEY_ENV", length = 100)
    @Comment("발신키를 담은 환경변수 키 이름 (값 아님)")
    private String senderKeyEnv;

    @Column(name = "PASSWD_ENV", length = 100)
    @Comment("발신 PW를 담은 환경변수 키 이름 (값 아님)")
    private String passwdEnv;

    @Column(name = "TEMPLATE_PREFIX", length = 50)
    @Comment("점포별 템플릿 코드 프리픽스 (선택)")
    private String templatePrefix;

    @Column(name = "USE_YN", length = 1)
    @Comment("사용 여부")
    private String useYn;

    @CreationTimestamp
    @Column(name = "CREATE_DATE", updatable = false)
    private LocalDateTime createDate;

    @UpdateTimestamp
    @Column(name = "UPDATE_DATE")
    private LocalDateTime updateDate;
}
