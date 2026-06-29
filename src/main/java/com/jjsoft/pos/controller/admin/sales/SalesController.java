package com.jjsoft.pos.controller.admin.sales;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jjsoft.pos.dto.product.ProductMstDto;
import com.jjsoft.pos.dto.sales.SalesDtlDto;
import com.jjsoft.pos.dto.sales.SalesMstDto;
import com.jjsoft.pos.dto.sales.condition.SalesSearchCondition;
import com.jjsoft.pos.enums.ResponseCode;
import com.jjsoft.pos.exception.GlobalException;
import com.jjsoft.pos.response.ApiResponse;
import com.jjsoft.pos.service.admin.sales.SalesService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 * admin 판메 관리 
 */
@RestController
@RequestMapping("/api/admin/sales")
@RequiredArgsConstructor
@Log4j2
public class SalesController {

	private final SalesService salesService;
	
	
	/** 판매 리스트 조회  */
    @GetMapping("/selectSalesMstList")
    public ResponseEntity<ApiResponse<Object>> selectSalesMstList(@ModelAttribute SalesSearchCondition condition) {
        log.info("selectProductList condition = {}" , condition.toString());
        List<SalesMstDto> list = salesService.selectSalesMstList(condition);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(list));
    }
    /** 판매 상세 리스트 조회  */
    @GetMapping("/selectSalesDtlList")
    public ResponseEntity<ApiResponse<Object>> selectSalesDtlList(@RequestParam(name = "salesId", required = false) Long salesId) {
    	log.info("selectDtlList salesId = {}" , salesId);
    	List<SalesDtlDto> list = salesService.selectSalesDtlList(salesId);
    	return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(list));
    }
    
    @PostMapping("/saveSales")
    public ResponseEntity<Void> saveSales(@RequestBody SalesMstDto salesMstDto ,  @AuthenticationPrincipal Jwt jwt) {
        try {
        	String userId = "";
        	if (jwt != null) {
    			userId = jwt.getClaim("email"); // email preferred_username
    	    }
        	salesService.saveOrUpdateSales(salesMstDto , userId);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw new GlobalException(ResponseCode.SAVE_ERROR);
		} 
        return ResponseEntity.ok().build();
    }
}
