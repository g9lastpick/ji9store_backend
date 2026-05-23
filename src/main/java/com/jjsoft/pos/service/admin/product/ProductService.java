package com.jjsoft.pos.service.admin.product;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jjsoft.pos.dto.product.ProductDtlDto;
import com.jjsoft.pos.dto.product.ProductMstDto;
import com.jjsoft.pos.dto.product.condition.ProductSearchCondition;
import com.jjsoft.pos.dto.user.UserDto;
import com.jjsoft.pos.entity.ProductDtlEntity;
import com.jjsoft.pos.entity.ProductMstEntity;
import com.jjsoft.pos.enums.ProductStatus;
import com.jjsoft.pos.enums.ResponseCode;
import com.jjsoft.pos.exception.GlobalException;
import com.jjsoft.pos.mapper.ProductAdminMapper;
import com.jjsoft.pos.repository.CategoryMstRepository;
import com.jjsoft.pos.repository.ProductDtlRepository;
import com.jjsoft.pos.repository.ProductMstRepository;
import com.jjsoft.pos.repository.StoreLocationMstRepository;
import com.jjsoft.pos.repository.StoreMstRepository;
import com.jjsoft.pos.repository.StorePartnerMstRepository;
import com.jjsoft.pos.util.ComUtil;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductMstRepository      productMstRepository;
    private final ProductDtlRepository      productDtlRepository;
    private final CategoryMstRepository     categoryMstRepository;
    private final StoreLocationMstRepository     locationMstRepository;
    private final StoreMstRepository        storeMstRepository;
    private final StorePartnerMstRepository storePartnerMstRepository;
    private final ProductAdminMapper        productAdminMapper; 
    private final ComUtil                   comUtil; 

    /** 상품 마스터 리스트 */
    public List<ProductMstDto> selectMstList(ProductSearchCondition condition) {
    	return productAdminMapper.selectProductList(condition);
    }
    /** 상품 상세 리스트 */
    public List<ProductDtlDto> selectDtlList(Long productId) {
    	return productAdminMapper.selectProductDtlList(productId);
    }
    
    
    /** 상품 마스터 리스트 조회용 상품 정보 */
    public List<ProductMstDto> selectMstList2(ProductSearchCondition condition) {
    	return productAdminMapper.selectProductList2(condition);
    }
    
    /** 상품 마스터 저장 */
    @Transactional
    public void saveOrUpdateMst(List<ProductMstDto> productMstDtos , String userId) {
        // 상품 마스터 정보 처리 (등록/수정)
        for (ProductMstDto productMstDto : productMstDtos) {
            ProductMstEntity productMstEntity;

            if (productMstDto.getProductId() != null && productMstDto.getProductId() > 0) {
                // 기존 상품 마스터 업데이트
                productMstEntity = productMstRepository.findById(productMstDto.getProductId())
                        .orElseThrow(() -> new EntityNotFoundException("Product not found"));
                productMstEntity.setUpdateDate(LocalDateTime.now());
                productMstEntity.setUpdateUser(userId);
            } else {
                // 신규 상품 마스터 등록
                productMstEntity = new ProductMstEntity();
                productMstEntity.setCreateDate(LocalDateTime.now());
                productMstEntity.setCreateUser(userId);
            }

            // 매장, 카테고리, 계열사 엔티티 조회
//            StoreMstEntity store = storeMstRepository.findById(productMstDto.getStoreId())
//                    .orElseThrow(() -> new EntityNotFoundException("Store not found"));
//            CategoryMstEntity category = categoryMstRepository.findById(productMstDto.getCategoryId())
//                    .orElseThrow(() -> new EntityNotFoundException("Category not found"));
//            StorePartnerMstEntity partner = storePartnerMstRepository.findById(productMstDto.getPartnerId())
//                    .orElseThrow(() -> new EntityNotFoundException("Store Partner not found"));

            
            ProductStatus status = productMstDto.getStatus().equals("ACTIVE") ? ProductStatus.ACTIVE : ProductStatus.FROZEN;
            
            // 상품 마스터 정보 설정                               productNm
            productMstEntity.setProductNm    (productMstDto.getProductNm());
            productMstEntity.setPproductCd   (productMstDto.getPproductCd());
            productMstEntity.setPvarcodeNo   (productMstDto.getPvarcodeNo());
            productMstEntity.setOrgPrice     (productMstDto.getOrgPrice()      == null? 0 : productMstDto.getOrgPrice());
            productMstEntity.setOrgSalesPrice(productMstDto.getOrgSalesPrice() == null? 0 : productMstDto.getOrgSalesPrice());
            productMstEntity.setStoreId      (productMstDto.getStoreId());
            productMstEntity.setCategoryId   (productMstDto.getCategoryId());
            productMstEntity.setPartnerId    (productMstDto.getPartnerId());
            productMstEntity.setDescription  (productMstDto.getDescription());
            productMstEntity.setStatus       (status);

            // 상품 마스터 저장
            productMstRepository.save(productMstEntity);
        }
    }
    
    /** 상품 상세 저장 lot 코드 
     * @throws Exception */
    @Transactional
    public void saveOrUpdateDtl(Long productId, List<ProductDtlDto> productDtlDtos ,String userId) throws Exception {
        // 상품 마스터 처리 (등록/수정)
        ProductMstEntity productMstEntity;

        if (productId != null) {
            // 기존 상품 마스터 업데이트
            productMstEntity = productMstRepository.findById(productId)
                    .orElseThrow(() -> new EntityNotFoundException("Product not found 상품  아이디 : " + productId));
            productMstEntity.setUpdateDate(LocalDateTime.now());
            productMstEntity.setUpdateUser(userId);
        } else {
            //error
        	throw new GlobalException(ResponseCode.NOT_FOUND_OBJECT);
        }

        // 상품 상세 정보 처리 (등록/수정)
        for (ProductDtlDto productDtlDto : productDtlDtos) {
            ProductDtlEntity productDtlEntity;
            String lotNo = "";
            boolean isNew = false;
            if (productDtlDto.getProductDtlId() != null && productDtlDto.getProductDtlId() > 0) {
                // 기존 상품 상세 정보 업데이트
                productDtlEntity = productDtlRepository.findById(productDtlDto.getProductDtlId())
                        .orElseThrow(() -> new EntityNotFoundException("Product Detail not found 상품 상세 아이디 : " + productDtlDto.getProductDtlId()));
                lotNo = productDtlDto.getLotNo();
                productDtlEntity.setUpdateDate(LocalDateTime.now());
                productDtlEntity.setUpdateUser(userId);
            } else {
                // 신규 상품 상세 정보 등록
                productDtlEntity = new ProductDtlEntity();
                productDtlEntity.setCreateDate(LocalDateTime.now());
                productDtlEntity.setCreateUser(userId);
                lotNo = generateLotNo(productMstEntity, productDtlDto);
                isNew = true;
            }
            
            
            
            
            productDtlEntity.setProductId(productMstEntity.getProductId());  // 연관된 상품 마스터 설정
            productDtlEntity.setLocationId(productDtlDto.getLocationId());
            productDtlEntity.setPProductCd(productDtlDto.getPProductCd());
            productDtlEntity.setPVarcodeNo(productDtlDto.getPVarcodeNo());
            if(isNew) {
            	productDtlEntity.setReceivedDate  (comUtil.strToLocalDateTime(productDtlDto.getReceivedDate())    );
            	productDtlEntity.setExpirationDate(comUtil.strToLocalDateTime(productDtlDto.getExpirationDate())  );
            }else {
            	String orgReceivedDate   = comUtil.localDateTimeToStr(productDtlEntity.getReceivedDate());
            	String orgExpirationDate = comUtil.localDateTimeToStr(productDtlEntity.getExpirationDate());
            	if(!orgReceivedDate.equals(productDtlDto.getReceivedDate())) {
            		productDtlEntity.setReceivedDate(comUtil.strToLocalDateTime(productDtlDto.getReceivedDate())  );
            	}
            	if(!orgExpirationDate.equals(productDtlDto.getExpirationDate())) {
            		productDtlEntity.setExpirationDate(comUtil.strToLocalDateTime(productDtlDto.getExpirationDate())  );
            	}
            }
            ProductStatus status = productDtlDto.getStatus().equals("ACTIVE") ? ProductStatus.ACTIVE : ProductStatus.FROZEN;
            
            productDtlEntity.setStatus(status);
            productDtlEntity.setBoxQty(productDtlDto.getBoxQty());
            productDtlEntity.setOrgStockQty(productDtlDto.getOrgStockQty() == null? 0 : productDtlDto.getOrgStockQty());
            productDtlEntity.setCurStockQty(productDtlDto.getCurStockQty() == null? 0 : productDtlDto.getCurStockQty());
            productDtlEntity.setCostPrice  (productDtlDto.getCostPrice()   == null? 0 : productDtlDto.getCostPrice());
            productDtlEntity.setOrgPrice   (productDtlDto.getOrgPrice()    == null? 0 : productDtlDto.getOrgPrice());
            productDtlEntity.setSalesPrice (productDtlDto.getSalesPrice()  == null? 0 : productDtlDto.getSalesPrice());
            productDtlEntity.setAgreedPrice(productDtlDto.getAgreedPrice() == null? 0 : productDtlDto.getAgreedPrice());
            productDtlEntity.setEtcPrice   (productDtlDto.getEtcPrice()    == null? 0 : productDtlDto.getEtcPrice());
            productDtlEntity.setDescription(productDtlDto.getDescription());
            productDtlEntity.setLotNo(lotNo);

            // 상품 상세 저장
            productDtlRepository.save(productDtlEntity); 
        }
    }
    
    private boolean isLotNoDuplicated(Long productId , String lotNo) {
        // 같은 상품에서 lotNo가 중복되는지 확인
        return productDtlRepository.existsByProductIdAndLotNo(productId, lotNo);
    }
    
    public String generateLotNo(ProductMstEntity productMstEntity , ProductDtlDto productDtlDto ) {
    	
        String storeId = String.valueOf(productMstEntity.getStore().getStoreId());
        String partnerId = String.valueOf(productMstEntity.getPartner().getPartnerId() );
        String categoryId = String.valueOf(productMstEntity.getCategory().getCategoryId() );
        String locationId = String.valueOf(productDtlDto.getLocationId());
        String receivedDate = productDtlDto.getReceivedDate().replace("-", ""); // 날짜를 yyyymmdd 형식으로

        String lotNo =  storeId + "_" + partnerId+ "_" + categoryId+ "_" + locationId+ "_" + receivedDate;
        
        if(isLotNoDuplicated(productMstEntity.getProductId() , lotNo)) {
        	throw new GlobalException(ResponseCode.PRODUCT_DTL_LOT_NO_DUP);  
        }
        
        return lotNo;
    }
    
    
    
    
    /** userList 조회 조건은 productNm으로 처리*/
    public List<UserDto> selectUserList(ProductSearchCondition condition) {
    	return productAdminMapper.selectUserList(condition.getProductNm());
    }
}
