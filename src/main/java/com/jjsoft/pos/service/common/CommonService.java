package com.jjsoft.pos.service.common;

import org.springframework.stereotype.Service;

import com.jjsoft.pos.mapper.ProductAdminMapper;
import com.jjsoft.pos.repository.CategoryMstRepository;
import com.jjsoft.pos.repository.ProductDtlRepository;
import com.jjsoft.pos.repository.ProductMstRepository;
import com.jjsoft.pos.util.ComUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommonService {

    private final ProductMstRepository  productRepository;
    private final ProductDtlRepository  productLotRepository;
    private final CategoryMstRepository categoryRepository;
    private final ProductAdminMapper productAdminMapper; 
    private final ComUtil comUtil;

    
    
}
