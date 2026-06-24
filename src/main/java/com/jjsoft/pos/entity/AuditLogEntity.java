package com.jjsoft.pos.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_log")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AuditLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("PK")
    private Long id;

    @Comment("사용자 ID")
    @Column(name = "USER_ID", length = 100)
    private String userId;

    @Comment("사용자 ID")
    @Column(name = "USER_NAME", length = 100)
    private String userName;

    @Comment("HTTP Method (GET, POST 등)")
    @Column(name = "ACTION_TYPE", length = 20)
    private String actionType;

    @Comment("요청 URL")
    @Column(name = "URL", length = 500)
    private String url;

    @Comment("요청 점포 ID (StoreContext 기준)")
    @Column(name = "STORE_ID")
    private Long storeId;

    @Comment("요청 파라미터 (JSON 또는 String)")
    @Column(name = "PARAMS", columnDefinition = "TEXT")
    private String params;

    @Comment("처리 결과 (SUCCESS / FAIL)")
    @Column(name = "RESULT", length = 10)
    private String result;

    @Comment("요청자 IP")
    @Column(name = "IP", length = 45)
    private String ip;

    @Comment("실패 사유 (인증/권한 실패 시)")
    @Column(name = "ERROR_MSG", length = 500)
    private String errorMsg;

    @Comment("생성일자")
    @CreationTimestamp
    @Column(name = "CREATE_DATE", updatable = false)
    private LocalDateTime createDate;
}
