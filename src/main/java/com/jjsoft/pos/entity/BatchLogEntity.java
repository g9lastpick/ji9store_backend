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
 * 배치 실행 로그
 */
@Entity
@Table(name = "batch_log")
@Comment("배치 실행 로그")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class BatchLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BATCH_LOG_ID")
    @Comment("로그 ID")
    private Long batchLogId;

    @Column(name = "BATCH_NAME", length = 100, nullable = false)
    @Comment("배치명")
    private String batchName;

    @Column(name = "START_TIME")
    @Comment("배치 시작 시간")
    private LocalDateTime startTime;

    @Column(name = "END_TIME")
    @Comment("배치 종료 시간")
    private LocalDateTime endTime;

    @Column(name = "STATUS", length = 20)
    @Comment("배치 상태 (RUNNING, SUCCESS, FAIL)")
    private String status;

    @Column(name = "RESULT_MSG", length = 1000)
    @Comment("결과 메시지")
    private String resultMsg;

    @Column(name = "ERROR_STACK", columnDefinition = "TEXT")
    @Comment("에러 상세 내용")
    private String errorStack;

    @Column(name = "EXECUTION_TIME")
    @Comment("소요 시간 (초)")
    private Long executionTime;

    @CreationTimestamp
    @Column(name = "CREATE_DATE", updatable = false)
    @Comment("생성일시")
    private LocalDateTime createDate;

    @Column(name = "CREATE_USER")
    @Comment("생성자")
    private String createUser;

}
