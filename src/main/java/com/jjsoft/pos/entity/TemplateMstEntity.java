package com.jjsoft.pos.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * 비즈톡 템플릿 마스터 Entity
 * template_mst 테이블 매핑
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Entity
@Table(name = "template_mst")
@Comment("비즈톡 템플릿 관리")
public class TemplateMstEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    @Comment("내부 식별자")
    private Long id;

    @Column(name = "STORE_ID")
    @Comment("STORE ID")
    private Long storeId;
    
    @Column(name = "LOCATION_ID")
    @Comment("지점 ID")
    private Long locationId;
    
    @Column(name = "API_SVR_NAME", length = 50)
    @Comment("biztalk / ...")
    private String apiSvrName;

    @Column(name = "SNS_TYPE", length = 50)
    @Comment("알림톡 / ...")
    private String snsType;

    @Column(name = "TMPL_CODE", length = 200, nullable = false , unique = true)
    @Comment("BIZTALK 템플릿 CODE")
    private String tmplCode;

    @Column(name = "TMPL_NAME", length = 200)
    @Comment("BIZTALK 템플릿 명")
    private String tmplName;
    
    @Column(name = "TMPL_TITLE", length = 1000)
    @Comment("강조 표기 타이틀")
    private String tmplTitle;

    @Column(name = "TMPL_CONTENT", length = 1300, nullable = false)
    @Comment("템플릿 내용")
    private String tmplContent;
    
    @Column(name = "WEB_BTN", length = 1000)
    @Comment("web 버튼 list 예) AC@채널추가@@WL@예약상품보기@https://g9system.com/mobile/orderList")
    private String webBtn;

    @Column(name = "USE_YN", length = 1)
    @Comment("사용 여부")
    private String useYn;

    @Column(name = "DESCRITION", columnDefinition = "TEXT")
    @Comment("설명")
    private String descrition;

    @Column(name = "CREATE_USER", length = 50)
    @Comment("생성자")
    private String createUser;

    @CreationTimestamp
    @Column(name = "CREATE_DATE", updatable = false)
    @Comment("생성일자")
    private LocalDateTime createDate;

    @Column(name = "UPDATE_USER", length = 50)
    @Comment("수정자")
    private String updateUser;

    @UpdateTimestamp
    @Column(name = "UPDATE_DATE")
    @Comment("수정일자")
    private LocalDateTime updateDate;
}
