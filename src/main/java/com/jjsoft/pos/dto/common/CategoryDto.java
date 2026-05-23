package com.jjsoft.pos.dto.common;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 카테고리 조회 DTO
 * DB category_mst 테이블 매핑
 */
@Data
public class CategoryDto {

    /** 카테고리 ID */
    private Long categoryId;

    /** 부모 카테고리 ID */
    private Long parentId;

    /** 매장 ID */
    private Long storeId;

    /** 카테고리 코드 */
    private String categoryCd;

    /** 카테고리 이름 */
    private String categoryNm;

    /** 경로 ID (예: 1-10-100) */
    private String pathId;

    /** 경로 이름 (예: 식품-상온-간편식) */
    private String pathNm;

    /** 레벨 */
    private Integer lvl;

    /** 정렬 순서 */
    private Integer sortOrder;

    /** 리프 여부 */
    private String isLeaf;

    /** 설명 */
    private String description;

    /** 사용 여부 */
    private String useYn;

    /** 생성일 */
    private LocalDateTime createDate;

}
