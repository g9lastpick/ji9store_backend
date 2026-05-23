package com.jjsoft.pos.dto.common;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ImageUploadRequest {

	private Long productId;                         // 상품 ID
	private Long specialDtlId;                         // 특가 ID
    private Integer sortOrder = 1;                  // 정렬 순서
    private String description;                     // 설명
    private String createUser;                      // 등록자
    private List<MultipartFile> imageFileList;             // 업로드 이미지 파일
    
    private Long groupbuyId;
    private Long drawId;
}
