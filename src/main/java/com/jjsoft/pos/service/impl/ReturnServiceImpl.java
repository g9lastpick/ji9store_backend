package com.jjsoft.pos.service.impl;

import org.springframework.stereotype.Service;

import com.jjsoft.pos.dto.ReturnDto;
import com.jjsoft.pos.entity.SalesMstEntity;
import com.jjsoft.pos.repository.ReturnMstRepository;
import com.jjsoft.pos.repository.SalesMstRepository;
import com.jjsoft.pos.service.WriteService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReturnServiceImpl implements WriteService<ReturnDto> {

    private final ReturnMstRepository returnRepository;
    private final SalesMstRepository salesRepository;

    @Override
    public void register(ReturnDto dto) {
    	
    	SalesMstEntity sales = salesRepository.findById(dto.getSalesId())
    		    .orElseThrow(() -> new IllegalArgumentException("판매 내역 없음"));
    	
//        ReturnEntity entity = ReturnEntity.builder()
//                .sales(sales)
//                .storeId(dto.getStoreId())
//                .totalQty(dto.getTotalQty())
//                .totalAmount(dto.getTotalAmount())
//                .returnReason(dto.getReturnReason())
//                .status(dto.getStatus())
//                .returnDate(dto.getReturnDate())
//                .build();
//
//        returnRepository.save(entity);
    }

    @Override
    public void update(ReturnDto dto) {
//        ReturnMstEntity entity = returnRepository.findById(dto.getReturnId())
//                .orElseThrow(() -> new IllegalArgumentException("반품 내역 없음"));
//
//        entity.setReturnReason(dto.getReturnReason());
//        entity.setStatus(dto.getStatus());
//        returnRepository.save(entity);
    }

    @Override
    public void delete(Long id) {
//        returnRepository.deleteById(id);
    }
}
