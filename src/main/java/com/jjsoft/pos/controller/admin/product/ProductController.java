package com.jjsoft.pos.controller.admin.product;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jjsoft.pos.dto.product.ProductDtlDto;
import com.jjsoft.pos.dto.product.ProductMstDto;
import com.jjsoft.pos.dto.product.condition.ProductSearchCondition;
import com.jjsoft.pos.dto.user.UserDto;
import com.jjsoft.pos.enums.ResponseCode;
import com.jjsoft.pos.exception.GlobalException;
import com.jjsoft.pos.mapper.ProductAdminMapper;
import com.jjsoft.pos.repository.ProductMstRepository;
import com.jjsoft.pos.response.ApiResponse;
import com.jjsoft.pos.service.admin.product.ProductService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 * admin 상품 관리 
 */
@RestController
@RequestMapping("/api/admin/products")
@RequiredArgsConstructor
@Log4j2
public class ProductController {

    private final ProductService productService;
    private final ProductMstRepository productMstRepository;
    private final ProductAdminMapper productMapper;
    
    /** 상품 리스트 조회  */
    @GetMapping("/selectMstList")
    public ResponseEntity<ApiResponse<Object>> selectMstList(@ModelAttribute ProductSearchCondition condition) {
        log.info("selectProductList condition = {}" , condition.toString());
        List<ProductMstDto> list = productService.selectMstList(condition);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(list));
    }
    /** 상품 상세 리스트 조회  */
    @GetMapping("/selectDtlList")
    public ResponseEntity<ApiResponse<Object>> selectDtlList(@RequestParam(name = "productId", required = false) Long productId) {
    	log.info("selectDtlList productId = {}" , productId);
    	List<ProductDtlDto> list = productService.selectDtlList(productId);
    	return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(list));
    }
    
    /**
     * 상품 검색 팝업용 목록 조회
     */
    @GetMapping("/filterList")
    public List<ProductDtlDto> filterList(@ModelAttribute ProductSearchCondition condition) {
        return productMapper.filterList(condition);
    }
    
    @PostMapping("/saveMst")
    public ResponseEntity<Void> saveMst(@RequestBody List<ProductMstDto> list , @AuthenticationPrincipal Jwt jwt) {
        try {
        	String userId = "";
        	if (jwt != null) {
    			userId = jwt.getClaim("email"); // email preferred_username
    	    }
        	productService.saveOrUpdateMst(list,userId);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw new GlobalException(ResponseCode.SAVE_ERROR);
		} 
        return ResponseEntity.ok().build();
    }
    @PostMapping("/saveDtl")
    public ResponseEntity<Void> saveDtl(@RequestBody  List<ProductDtlDto> list , @AuthenticationPrincipal Jwt jwt) {
    	try {
    		String userId = "";
        	if (jwt != null) {
    			userId = jwt.getClaim("email"); // email preferred_username
    	    }
    		if(list.size() <= 0) {
    			throw new GlobalException(ResponseCode.SAVE_ERROR);
    		}
    		Long productId = list.get(0).getProductId();
    		if(productId == null || productId < 0) {
    			throw new GlobalException(ResponseCode.SAVE_ERROR);
    		}
    		productService.saveOrUpdateDtl(productId , list, userId);
    	} catch (Exception e) {
    		// TODO: handle exception
    		e.printStackTrace();
    		throw new GlobalException(ResponseCode.SAVE_ERROR);
    	} 
    	return ResponseEntity.ok().build();
    }
    
    /* product mst 상품명 중복 체크 */
    @PostMapping("/duplicateCheck")
    public ResponseEntity<ApiResponse<Object>> duplicateCheck(@RequestBody  List<ProductMstDto> list){
    	
    	boolean flag  = false;
    	for(ProductMstDto dto  :  list) {
    		
    		if(dto.is_isNew()) {
    			flag = productMstRepository.existsProduct(dto.getStoreId() , dto.getPartnerId() , dto.getCategoryId() , dto.getProductNm());
    		}else {
    			flag = productMstRepository.existsProductExceptSelf(dto.getStoreId() , dto.getPartnerId() , dto.getCategoryId() , dto.getProductNm() , dto.getProductId());
    		}
    		if(flag) break;
    	}
    	
    	if(flag) return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(true));
    	else     return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(false));
    }
    
    @PutMapping
    public ResponseEntity<Void> updateProduct(@ModelAttribute ProductMstDto dto) {
//        productService.update(dto);
        return ResponseEntity.ok().build();
    }
    @DeleteMapping
    public ResponseEntity<Void> deleteProduct(@RequestBody Long id) {
//    	productService.delete(id);
    	return ResponseEntity.ok().build();
    }

    
    /** 상품 리스트 조회 - 조회용임  */
    @GetMapping("/selectMstList2")
    public ResponseEntity<ApiResponse<Object>> selectMstList2(@ModelAttribute ProductSearchCondition condition) {
        log.info("selectProductList condition = {}" , condition.toString());
        List<ProductMstDto> list = productService.selectMstList2(condition);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(list));
    }
    
    
    /** userList 조회 */
    @GetMapping("/selectUserList")
    public ResponseEntity<ApiResponse<Object>> selectUserList(@ModelAttribute ProductSearchCondition condition) {
        log.info("selectUserList condition = {}" , condition.toString());
        List<UserDto> list = productService.selectUserList(condition);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(list));
    }
    
    

    
}
