package com.jjsoft.pos.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.repository.query.Param;

import com.jjsoft.pos.dto.product.ProductDtlDto;
import com.jjsoft.pos.dto.product.ProductMstDto;
import com.jjsoft.pos.dto.product.condition.ProductSearchCondition;
import com.jjsoft.pos.dto.user.UserDto;

@Mapper
public interface ProductAdminMapper {
	 List<ProductMstDto> selectProductList(ProductSearchCondition condition);
	 List<ProductMstDto> selectProductList2(ProductSearchCondition condition);
	 List<ProductDtlDto> selectProductDtlList(Long productId);
	 
	 List<ProductDtlDto> filterList(ProductSearchCondition condition);
	 List<UserDto> selectUserList(@Param("searchTxt") String searchTxt);
}
