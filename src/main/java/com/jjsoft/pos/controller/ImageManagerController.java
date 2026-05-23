package com.jjsoft.pos.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jjsoft.pos.dto.common.ImageUploadRequest;
import com.jjsoft.pos.dto.product.ProductImageDto;
import com.jjsoft.pos.entity.DrawMstEntity;
import com.jjsoft.pos.entity.GroupbuyMstEntity;
import com.jjsoft.pos.entity.SpecialDtlEntity;
import com.jjsoft.pos.repository.DrawMstRepository;
import com.jjsoft.pos.repository.GroupbuyMstRepository;
import com.jjsoft.pos.repository.ProductImageRepository;
import com.jjsoft.pos.repository.SpecialDtlRepository;
import com.jjsoft.pos.response.ApiResponse;
import com.jjsoft.pos.service.common.ImageManagerService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 * 이미지 관리 컨트롤러 
 */
@RestController
@RequestMapping("/api/image")
@RequiredArgsConstructor
@Log4j2
public class ImageManagerController {

	private final ImageManagerService imageManagerService ;
	private final ProductImageRepository productImageRepository ;
	 private final SpecialDtlRepository specialDtlRepository;
//	 private final GroupbuyMstRepository groupbuyMstRepository;
//	 private final DrawMstRepository drawMstRepository;
	
//	@Value("${app.base-url}")
//	private String baseUrl;
	
	@GetMapping("/selectProductImgs/{productId}")
	public ResponseEntity<ApiResponse<Object>> selectProductImgs(@PathVariable("productId") Long productId) {
		
		try {
			  List list = productImageRepository.findImagesByProductId(productId).stream()
		        .map(img -> ProductImageDto.builder()
		                .imageId(img.getImageId())
		                .imageUrl(img.getImageUrl())   // ✅ URL 앞에 도메인 붙이기
		                .build()
		            ).toList();
			return ResponseEntity.ok(ApiResponse.ok(list));
		}catch(Exception e) {
			e.printStackTrace();
			return ResponseEntity.ok(ApiResponse.fail("이미지 조회 실패 productId : " + productId));

		}
	}
	

	
	@PostMapping("/product/delete/{imgId}")
	public ResponseEntity<ApiResponse<Object>> deleteProductImg(@PathVariable("imgId") Long imgId) {
		boolean flag = imageManagerService.deleteImageById(imgId);
	    return ResponseEntity.ok(ApiResponse.ok(flag));
	}
	
	@PostMapping("/product/upload")
    public ResponseEntity<ApiResponse<Object>> uploadImage(@ModelAttribute ImageUploadRequest request) throws Exception {
		
		try {
			
			boolean flag =  imageManagerService.saveImages(request);
			
			return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(flag));
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.fail("image 저장 에러"));
    }
	
	
	
	
	
	
	
	
	
	@GetMapping("/selectSpecialImgs/{specailDtlId}")
	public ResponseEntity<ApiResponse<Object>> selectSpecialImgs(@PathVariable("specailDtlId") Long specailDtlId) {
		
		try {
			Optional<SpecialDtlEntity> opt = specialDtlRepository.findById(specailDtlId);

			List<ProductImageDto> list = opt
			    .map(img -> {
			        ProductImageDto dto = ProductImageDto.builder()
			            .imageId(img.getSpecialDtlId())
			            .imageUrl(img.getImageUrl())
			            .build();
			        return List.of(dto);   // ✅ 바로 List 반환
			    })
			    .orElseGet(List::of);
			return ResponseEntity.ok(ApiResponse.ok(list));
		}catch(Exception e) {
			e.printStackTrace();
			return ResponseEntity.ok(ApiResponse.fail("이미지 조회 실패 specailDtlId : " + specailDtlId));
			
		}
	}
	
	/** 특가상품 썸네일 등록 */
	@PostMapping("/product/special/upload")
    public ResponseEntity<ApiResponse<Object>> uploadSpecialDtlImage(@ModelAttribute ImageUploadRequest request) throws Exception {
		
		try {
			
			boolean flag =  imageManagerService.saveSpecialDtlImages(request);
			
			return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(flag));
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.fail("image 저장 에러"));
    }
	/** 특가상품 썸네일 삭제 */
	@PostMapping("/product/special/delete/{specialDtlId}")
	public ResponseEntity<ApiResponse<Object>> deleteSpecialImageById(@PathVariable("specialDtlId") Long specialDtlId) {
		boolean flag = imageManagerService.deleteSpecialDtlImageById(specialDtlId);
	    return ResponseEntity.ok(ApiResponse.ok(flag));
	}
	
	
	
	
	
	
	
	
	
	
	
	
	@GetMapping("/selectGroupbuyImgs/{groupbuyId}")
	public ResponseEntity<ApiResponse<Object>> selectGroupbuyImgs(@PathVariable("groupbuyId") Long groupbuyId) {
		
//		try {
//			Optional<GroupbuyMstEntity> opt = groupbuyMstRepository.findById(groupbuyId);
//
//			List<ProductImageDto> list = opt
//			    .map(img -> {
//			        ProductImageDto dto = ProductImageDto.builder()
//			            .imageId(img.getGroupbuyId())
//			            .imageUrl(img.getImageUrl())
//			            .build();
//			        return List.of(dto);   // ✅ 바로 List 반환
//			    })
//			    .orElseGet(List::of);
//			return ResponseEntity.ok(ApiResponse.ok(list));
//		}catch(Exception e) {
//			e.printStackTrace();
//			return ResponseEntity.ok(ApiResponse.fail("공동구매 이미지 조회 실패 groupbuyId : " + groupbuyId));
//			
//		}
		return null;
	}
	
	/** 공동구매 썸네일 등록 */
	@PostMapping("/product/groupbuy/upload")
    public ResponseEntity<ApiResponse<Object>> uploadGroupbuyImage(@ModelAttribute ImageUploadRequest request) throws Exception {
		
//		try {
//			
//			boolean flag =  imageManagerService.saveGroupbuyImages(request);
//			
//			return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(flag));
//		}catch(Exception e) {
//			e.printStackTrace();
//		}
//		
//		return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.fail("image 저장 에러"));
		return null;
    }
	/** 공동구매 썸네일 삭제 */
	@PostMapping("/product/groupbuy/delete/{groupbuyId}")
	public ResponseEntity<ApiResponse<Object>> deleteGroupbuyImageById(@PathVariable("groupbuyId") Long groupbuyId) {
//		boolean flag = imageManagerService.deleteGroupbuyImageById(groupbuyId);
//	    return ResponseEntity.ok(ApiResponse.ok(flag));
		return null;
	}
	
	
	
	
	
	
	
	
	
	
	@GetMapping("/selectDrawImgs/{drawId}")
	public ResponseEntity<ApiResponse<Object>> selectDrawImgs(@PathVariable("drawId") Long drawId) {
		
//		try {
//			Optional<DrawMstEntity> opt = drawMstRepository.findById(drawId);
//
//			List<ProductImageDto> list = opt
//			    .map(img -> {
//			        ProductImageDto dto = ProductImageDto.builder()
//			            .imageId(img.getDrawId())
//			            .imageUrl(img.getImageUrl())
//			            .build();
//			        return List.of(dto);   // ✅ 바로 List 반환
//			    })
//			    .orElseGet(List::of);
//			return ResponseEntity.ok(ApiResponse.ok(list));
//		}catch(Exception e) {
//			e.printStackTrace();
//			return ResponseEntity.ok(ApiResponse.fail("드로우 이미지 조회 실패 drawId : " + drawId));
//			
//		}
		return null;
	}
	
	/** 드로우 썸네일 등록 */
	@PostMapping("/product/draw/upload")
    public ResponseEntity<ApiResponse<Object>> uploadDrawImage(@ModelAttribute ImageUploadRequest request) throws Exception {
		
//		try {
//			
//			boolean flag =  imageManagerService.saveDrawImages(request);
//			
//			return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(flag));
//		}catch(Exception e) {
//			e.printStackTrace();
//		}
//		
//		return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.fail("image 저장 에러"));
		return null;
    }
	/** 드로우 썸네일 삭제 */
	@PostMapping("/product/draw/delete/{drawId}")
	public ResponseEntity<ApiResponse<Object>> deleteDrawImageById(@PathVariable("drawId") Long drawId) {
//		boolean flag = imageManagerService.deleteDrawImageById(drawId);
//	    return ResponseEntity.ok(ApiResponse.ok(flag));
return null;
	}
}
