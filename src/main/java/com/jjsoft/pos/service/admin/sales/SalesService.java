package com.jjsoft.pos.service.admin.sales;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.stereotype.Service;

import com.jjsoft.pos.dto.sales.SalesDtlDto;
import com.jjsoft.pos.dto.sales.SalesMstDto;
import com.jjsoft.pos.dto.sales.condition.SalesSearchCondition;
import com.jjsoft.pos.entity.SalesDtlEntity;
import com.jjsoft.pos.entity.SalesMstEntity;
import com.jjsoft.pos.entity.UserMstEntity;
import com.jjsoft.pos.enums.PaymentType;
import com.jjsoft.pos.enums.ResponseCode;
import com.jjsoft.pos.enums.SalesStatus;
import com.jjsoft.pos.enums.SalesType;
import com.jjsoft.pos.exception.GlobalException;
import com.jjsoft.pos.mapper.ProductAdminMapper;
import com.jjsoft.pos.mapper.SalesAdminMapper;
import com.jjsoft.pos.repository.ProductDtlRepository;
import com.jjsoft.pos.repository.ProductMstRepository;
import com.jjsoft.pos.repository.SalesDtlRepository;
import com.jjsoft.pos.repository.SalesMstRepository;
import com.jjsoft.pos.repository.StoreMstRepository;
import com.jjsoft.pos.repository.UserMstRepository;
import com.jjsoft.pos.util.ComUtil;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class SalesService {

    private final SalesMstRepository     salesMstRepository;
    private final SalesDtlRepository     salesDtlRepository;
    private final StoreMstRepository     storeMstRepository;
    private final ProductMstRepository   productMstRepository;
    private final ProductDtlRepository   productDtlRepository;
    private final UserMstRepository      userMstRepository;
    
    
    private final ProductAdminMapper        productAdminMapper; 
    private final SalesAdminMapper        salesAdminMapper; 
    private final ComUtil                   comUtil; 

    /**  마스터 리스트 */
    public List<SalesMstDto> selectSalesMstList(SalesSearchCondition condition) {
    	
    	List<SalesMstDto> list = salesAdminMapper.selectSalesMstList(condition); 
    	list.forEach(x->{
    		x.setSalesStatus(SalesStatus.getSalesStatusNameFromKey(x.getSalesStatus())) ; 
    		x.setPaymentType(PaymentType.getPaymentNameFromKey    (x.getPaymentType())); 
    		x.setSalesType   (SalesType.getSalesTypeNameFromKey   (x.getSalesType())); 
    	});
    	return list;
    }
    /**  상세 리스트 */
    public List<SalesDtlDto> selectSalesDtlList(Long salesId) {
    	List<SalesDtlDto> list = salesAdminMapper.selectSalesDtlList(salesId); 
    	list.forEach(x->{
    		x.setPaymentType(PaymentType.getPaymentNameFromKey    (x.getPaymentType())); 
    		x.setSalesType   (SalesType.getSalesTypeNameFromKey   (x.getSalesType())); 
    	});
    	return list;
    }
    
    /**  sales 마스터 , detail 저장 */
    @Transactional
    public Long saveOrUpdateSales(SalesMstDto dto , String userId) {
        SalesMstEntity mst;
        long locationId = dto.getLocationId();
        //user id 생성
        UserMstEntity userEntity = userMstRepository.getUserByUserId(dto.getUserId()).orElse(null);
        if(userEntity == null) {
        	userEntity = UserMstEntity.builder()
        		.userId(dto.getUserId())
        		.name(dto.getUserId())
        		.password("1234")
        		.createUser(userId)
        		.build();
        	
        	userMstRepository.save(userEntity);
        }

        PaymentType   pType;
        SalesType     sType;
        SalesStatus sStatus;
        
        long pTypeCount  = dto.getDtlList().stream().map(SalesDtlDto::getPaymentType).filter(Objects::nonNull).distinct().count();
        long sTypeCount  = dto.getDtlList().stream().map(SalesDtlDto::getSalesType)  .filter(Objects::nonNull).distinct().count();
        long statusCount = dto.getDtlList().stream().map(SalesDtlDto::getSalesStatus).filter(Objects::nonNull).distinct().count();

    	if (pTypeCount == 1) pType = PaymentType.getPaymentFromKey(dto.getDtlList().get(0).getPaymentType());
    	else                 pType = PaymentType.GRP;//default
        	
    	if (sTypeCount == 1) sType = SalesType.getSalesTypeFromKey(dto.getDtlList().get(0).getSalesType());
    	else                 sType = SalesType.VISIT;//default
    	
    	if (statusCount == 1) sStatus = SalesStatus.getSalesStatusFromKey(dto.getDtlList().get(0).getSalesStatus());
    	else                  sStatus = SalesStatus.COMPLETE;//default
    	
        
        // 1. MST 저장 or 수정
        if (dto.getSalesId() != null && dto.getSalesId() > 0) {
            mst = salesMstRepository.findById(dto.getSalesId())
                    .orElseThrow(() -> new GlobalException(ResponseCode.NOT_FOUND_ENTITY));
            mst.setUpdateDate (LocalDateTime.now());
            mst.setUpdateUser (userId);
            mst.setPaymentType(pType);
            mst.setSalesType  (sType);
            mst.setSalesStatus(sStatus);
            mst.setDescription(dto.getDescription());
            // 필요시 가격, 결제 방식 등 수정
        } else {
            mst = SalesMstEntity.builder()
//                    .store       (storeMstRepository.findById(dto.getStoreId()).orElseThrow())
                    .storeId       (dto.getStoreId())
                    .userId        (userEntity.getUserId())
                    .paymentType (pType)
                    .salesType   (sType)
                    .salesStatus (sStatus)
                    .salesDate   (LocalDateTime.now())
                    .createDate  (LocalDateTime.now())
                    .createUser  (userId)
                    .description (dto.getDescription())
                    .build();
        }

        salesMstRepository.save(mst);
        
        if(dto.getDtlList() == null || dto.getDtlList().size() == 0) {
        	log.info("saveOrUpdateSales ===> dto.getDtlList() 정보 없음. sales 마스터만 저장됨.");
        	return mst.getSalesId();
        }
        
        //sales dtl delete
        salesDtlRepository.deleteAllBySalesId(mst.getSalesId());
        //sales dtl create
        
        int lineNo = 1;
        Integer totalSalesPrice = 0;
        Integer totalSalesQty = 0;
        for (SalesDtlDto d : dto.getDtlList()) {
        	totalSalesQty   += d.getQty();
        	 Integer tempPrice = d.getInputSalesPrice() == null || d.getInputSalesPrice() <= 0 ? d.getOrgSalesPrice() : d.getInputSalesPrice();
        	 totalSalesPrice += d.getQty() * tempPrice;
        	 d.setUnitPrice(tempPrice);
        	processSale(d, mst, locationId , userId);
        }
        
        mst.setTotalPrice(totalSalesPrice);
        mst.setTotalQty(totalSalesQty);
        return mst.getSalesId();
    }
    
    
    
    /**
     * productId, saleQty 입력받아 FIFO로 sales_dtl 생성
     */
    @Transactional
    public void processSale(SalesDtlDto sdto , SalesMstEntity salesMst, Long locationId ,String userId) {
    	Long productId = sdto.getProductId(); 
    	int remainQty = sdto.getQty();
    	
        List<Map<String, Object>> lots = salesAdminMapper.selectAvailableLots(productId, locationId);

        if (lots == null || lots.isEmpty()) {
            throw new IllegalStateException("사용 가능한 재고가 없습니다.");
        }

        int lineNo = 1;
        for (Map<String, Object> lot : lots) {
        	
        	if (remainQty == 0) break;
        	
            Long productDtlId = ((Number) lot.get("PRODUCT_DTL_ID")).longValue();
            int available     = ((Number) lot.get("AVAILABLE_QTY")).intValue();

            if (available <= 0) continue;

            int useQty = Math.min(available, remainQty);
            
            if (useQty <= 0) continue;

//            Integer salesPrice = useQty * (sdto.getInputSalesPrice() == null || sdto.getInputSalesPrice() <= 0 ? sdto.getOrgSalesPrice() : sdto.getInputSalesPrice());
            
            int unitPrice  = sdto.getUnitPrice()       == null || sdto.getUnitPrice() <= 0 
            				? sdto.getOrgSalesPrice() : sdto.getUnitPrice();
            int inputPrice = sdto.getInputSalesPrice() == null ? 0 : sdto.getInputSalesPrice();
            int orgPrice   = sdto.getOrgSalesPrice()   == null ? 0 : sdto.getOrgSalesPrice();
            int discount   = sdto.getDiscountPrice()   == null ? 0 : sdto.getDiscountPrice();
            
            int basePrice  = inputPrice > 0 ? inputPrice : orgPrice;
            int salesPrice = useQty * basePrice;
            int totalDiscount = discount * useQty;
            
            
        	SalesDtlEntity newDtl = SalesDtlEntity.builder()
	          .salesId      (salesMst.getSalesId())
	          .productId    (sdto.getProductId())
	          .productDtlId (productDtlId)
	          .lineNo       (lineNo)
	          .salesType    (SalesType.getSalesTypeFromKey(sdto.getSalesType()))
	          .paymentType  (PaymentType.getPaymentFromKey(sdto.getPaymentType()))
	          .unitPrice    (unitPrice)//원래 판매가격
	          .qty          (useQty)
	          .salesPrice   (salesPrice)//orgSalesPrice 가 현장가인데 가격을 입력할 수 있게 됭어서 inputSalesPrice로 바꿈
	          .discountPrice(totalDiscount)
	          .description  (sdto.getDescription())
	          .createDate   (LocalDateTime.now())
	          .createUser   (userId)
	          .build();
        	salesDtlRepository.save(newDtl);
            
            remainQty -= useQty;
            lineNo++;

        }

        if (remainQty > 0) {
            throw new IllegalStateException("재고 부족으로 " + remainQty + "개는 판매할 수 없습니다.");
        }
    }
    
    
  
    
    
    
}
