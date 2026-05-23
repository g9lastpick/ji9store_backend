package com.jjsoft.pos.service.admin.summary;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jjsoft.pos.dto.sales.SalesDtlDto;
import com.jjsoft.pos.dto.summary.DailySalesItemExcelDto;
import com.jjsoft.pos.dto.summary.ProductStockDto;
import com.jjsoft.pos.dto.summary.SalesResult;
import com.jjsoft.pos.dto.summary.SummaryChartDto;
import com.jjsoft.pos.dto.summary.SummaryDataDto;
import com.jjsoft.pos.dto.summary.SummarySearchCondition;
import com.jjsoft.pos.entity.DailySalesItemEntity;
import com.jjsoft.pos.entity.ProductMstEntity;
import com.jjsoft.pos.entity.ReturnDtlEntity;
import com.jjsoft.pos.entity.ReturnMstEntity;
import com.jjsoft.pos.entity.SalesDtlEntity;
import com.jjsoft.pos.entity.SalesMstEntity;
import com.jjsoft.pos.entity.StoreLocationMstEntity;
import com.jjsoft.pos.enums.PaymentType;
import com.jjsoft.pos.enums.ResponseCode;
import com.jjsoft.pos.enums.SalesStatus;
import com.jjsoft.pos.enums.SalesType;
import com.jjsoft.pos.exception.GlobalException;
import com.jjsoft.pos.mapper.SummaryMapper;
import com.jjsoft.pos.repository.DailySalesItemRepository;
import com.jjsoft.pos.repository.ProductMstRepository;
import com.jjsoft.pos.repository.ReturnDtlRepository;
import com.jjsoft.pos.repository.ReturnMstRepository;
import com.jjsoft.pos.repository.SalesDtlRepository;
import com.jjsoft.pos.repository.SalesMstRepository;
import com.jjsoft.pos.repository.StoreLocationMstRepository;
import com.jjsoft.pos.service.admin.sales.SalesService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SummaryService {
	
	private final SummaryMapper summaryMapper;
	private final DailySalesItemRepository dailySalesItemRepository;
	private final ProductMstRepository productMstRepository;
	private final SalesDtlRepository salesDtlRepository;
	private final SalesMstRepository salesMstRepository;
	private final StoreLocationMstRepository storeLocationMstRepository;
	private final ReturnMstRepository returnMstRepository;
	private final ReturnDtlRepository returnDtlRepository;
	private final SalesService salesService;
	
	
	
	
	/** sales 타입별 누적 집계 조회 - 첫 페이지 진입시만 조회 */
	public List<SummaryChartDto> getTotalAggregationChart( SummarySearchCondition condition) {
		return summaryMapper.getTotalAggregationChart(condition);
	    
	}
	
	/** 월별 누적집계 조회  */
	public List<SummaryChartDto> getTotalMonthAggregationChart( SummarySearchCondition condition) {
		return summaryMapper.getTotalMonthAggregationChart(condition);
		
	}
	
	/** 기간별 매출실적 조회 - 상단 검색 조건에 의해 변경됨. */
	public List<SummaryChartDto> getTotalSalesSumChart( SummarySearchCondition condition) {
		return summaryMapper.getTotalSalesSumChart(condition);
	    
	}
	
	/** 기간별 상품실적 - 상단 검색 조건에 의해 변경됨.  */
	public List<SummaryChartDto> getProductTotalSalesSumChart( SummarySearchCondition condition) {
		return summaryMapper.getProductTotalSalesSumChart(condition);
	    
	}
	
	/** 영업실적 현황 그리드 데이터 */
	public List<SummaryDataDto> getTotalSales( SummarySearchCondition condition) {
		return summaryMapper.getTotalSales(condition);
		
	}
	/** 상품별 판매 현황 그리드 데이터 */
	public List<SummaryDataDto> getTotalProductSales( SummarySearchCondition condition) {
		return summaryMapper.getTotalProductSales(condition);
		
	}
	/** 고객별 구매 현황 그리드 데이터 */
	public List<SummaryDataDto> getCustomerSales( SummarySearchCondition condition) {
		return summaryMapper.getCustomerSales(condition);
		
	}

	
	
	public LocalDate getSalesDateAsLocalDate(String date) {
	    return LocalDate.parse(date); // 🔄 실제 저장용
	}
	

	
	/** 엑셀 업로드 데이터 조회 */
	public List<DailySalesItemExcelDto> getExcelDataList( String  day) {
		return summaryMapper.getExcelDataList(day);
		
	}
	
	/** db 데이터 조회 */
	public List<DailySalesItemExcelDto> getDBDataList( String  day) {
		return summaryMapper.getDBDataList(day);
		
	}
	
    
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
     * excel 업로드시 바코드 존재 여부 체크
     */
	public List<Map<String, Object>> checkVarcodeList(List<String> varcodeList) {

	    List<String> existList = summaryMapper.selectExistVarcodeList(varcodeList);

	    Set<String> existSet = new HashSet<>(existList);

	    List<Map<String, Object>> result = new ArrayList<>();

	    for (String varcode : varcodeList) {

	        Map<String, Object> map = new HashMap<>();
	        map.put("barcode"          , varcode);
	        map.put("barcodeCheck"     , existSet.contains(varcode));

	        result.add(map);
	    }

	    return result;
	}
	
	
	
	
	
	
	/** excel data upload */
	@Transactional
	public void saveOrUpdateFromExcel(List<DailySalesItemExcelDto> dtoList, String createUser) {
		
	    if (dtoList.isEmpty()) return;
	    List<DailySalesItemEntity> entities = null;
	    
	    try {
	    	entities = dtoList.stream()
		            .map(dto -> DailySalesItemEntity.builder()
		                    .salesDate(getSalesDateAsLocalDate(dto.getSalesDate()))
		                    .locationId(dto.getLocationId())
		                    .categoryNm(dto.getCategoryNm())
		                    .productNm(dto.getProductNm())
		                    .barcode(dto.getBarcode())
		                    .unitPrice(dto.getUnitPrice())
		                    .qty(dto.getQty())
		                    .totalAmount(dto.getTotalAmount())
		                    .realAmount(dto.getRealAmount())
		                    .discountAmount(dto.getDiscountAmount())
		                    .refundAmount(dto.getRefundAmount())
		                    .refundQty(dto.getRefundQty())
		                    .processYn("N")
		                    .build())
		            .toList();

		    dailySalesItemRepository.saveAll(entities);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			
			if(e.getMessage().indexOf("foreign key") >= 0 ) {
				throw new GlobalException(ResponseCode.BAD_REQUEST , "데이터 적재 중 오료 발생 , 지점코드가 없습니다.");
			}
			throw new GlobalException(ResponseCode.BAD_REQUEST , "데이터 적재 중 오료 발생 데이터를 확인하세요");
			
		}
	    
	    // 날짜별 그룹
	    Map<LocalDate, List<DailySalesItemEntity>> dateGroup =
	            entities.stream().collect(Collectors.groupingBy(DailySalesItemEntity::getSalesDate));
	    
	    //지점별 그룹핑
	    for (LocalDate date : dateGroup.keySet()) {

	        Map<Long, List<DailySalesItemEntity>> locationGroup =
	                dateGroup.get(date)
	                        .stream()
	                        .collect(Collectors.groupingBy(DailySalesItemEntity::getLocationId));
	        
	        for (Long locationId : locationGroup.keySet()) {

		        List<DailySalesItemEntity> items = locationGroup.get(locationId);
		        //판매 마스터 생성
		        StoreLocationMstEntity locationMst = storeLocationMstRepository
		                .findById(locationId)
		                .orElseThrow(() ->
		                        new GlobalException(
		                                ResponseCode.NOT_FOUND,
		                                "지점코드가 존재하지 않습니다. 지점코드 = [" + locationId + "] 엑셀 데이터를 확인하세요"
		                        )
		                );
		        SalesMstEntity salesMst = createVisitSalesMst(locationMst.getStoreId() ,"UNKNOWN", date  );
		     // 🔥 누적 변수
		        int totalQty       = 0;
		        int totalPrice     = 0;
		        int totalDiscount  = 0;
		        for (DailySalesItemEntity item : items) {
		        	
		        	//이미 예약완료되어 판매처리된 건이 엑셀 수량과 일치하는경우 판매마스터를 생성 안했는데
		        	//이경우는 금액을 차감하는 로직으로 간다.,...
		        	// 하지만 n빵으로 들어가기 때문에 어떤 사람이 차감된지는 알수없지만 총 실 판매금액은 맞지 여기서 빼버리면
		        	//이 부분에서 엑셀 데이터의 판매 수량과 현재 예약 수량이 일치할 경우 할인금액만큼 금액 차감 시켜줘야함.
		        	//아니면 마이너스 판매가 들어가게끔 해야할텐데... 어떻게 해야할까?
		        	
		        	SalesResult result = processSalesItem(item , salesMst);
		        	

		            if(result != null && result.getErrorCode() > 0 && result.getErrorCode() != 100){
		                String msg = "";
		                if(result.getErrorCode() == 1)      msg = item.getProductNm() + "(" + item.getBarcode() +  ") 상품에 데이터에 문제가 있습니다 \n 관리자에게 문의하세요";
		                else if(result.getErrorCode() == 2) msg = item.getProductNm() + "(" + item.getBarcode() +  ") 상품 엑셀 수량이 0 입니다.";
		                else if(result.getErrorCode() == 3) msg = item.getProductNm() + "(" + item.getBarcode() +  ") 상품에 바코드정보가 없습니다. ";
		                else if(result.getErrorCode() == 4) msg = item.getProductNm() + "(" + item.getBarcode() +  ") 상품에 가용재고가 없습니다. ";
		                else if(result.getErrorCode() == 5) msg = item.getProductNm() + "(" + item.getBarcode() +  ") 상품 수량("+item.getQty()+")이 가용재고("+result.getAbailableQty()+")보다 많이 입력되었습니다  ";
		                else msg = item.getProductNm() + "(" + item.getBarcode() +  ") 상품에 문제가 있습니다. \n관리자에게 문의하세요 ";
		                throw new GlobalException(ResponseCode.NOT_FOUND , msg);
		            }
		          //result.getErrorCode() 10인경우 엑셀에서 올린 총 수량이 이미 잡혀있는경우라서 스킵해야함.
		            else if(result.getErrorCode() == 100){
		            	continue;
		            }
		            
//		            // 반품 처리 - 안하기로함.. 할인만 하는거로
//		        	if(item.getRefundQty() > 0){
//		        		salesDtlRepository.flush();
//		        	    processReturn(item, salesMst);
//		        	}
		            

		            totalQty      += result.getSaleQty();
		            totalPrice    += result.getSalesPrice();
		            totalDiscount += result.getDiscountPrice();
		            
		            
//		            if(!processSalesItem(item , salesMst)) {
//		            	String msg = item.getProductNm() + "에" + item.getQty() + "  데이터가 맞지않습니다.";
//		            	throw new GlobalException(ResponseCode.NOT_FOUND , msg);
//		            }
		        }
		        
		        // mst 총계 업데이트
		        salesMst.setTotalQty(totalQty);
		        salesMst.setTotalPrice(totalPrice);
		        salesMst.setDiscountPrice(totalDiscount);
//		        salesMst.setFinalPrice(totalPrice);

		        salesMstRepository.save(salesMst);
		    }
	    }
	    
	}
	
	/** 
	 * 상품별 판매 처리 로직  - 반품은 다른 함수에서 처리하고 해당 함수는 판매 및 할인 처리
	 * --------------------------------------------------------
	 * 엑셀 업로드 데이터를 기준으로 POS 판매 데이터를 보정하는 로직
	 * 엑셀 판매수량 = POS 판매(특가판매 처리 및 매장에서 포스 시스템을 이용한 방문판매 등록포함) + 등록안된 방문 판매도 같이 들어옴.
	 * 
	 * 판매등록 수량은 날짜 기준으로 = 엑셀 판매 수량 - 반품 수량(엑셀에 존재) - POS 판매 수량(계산해야함) 
	 */
	private SalesResult processSalesItem(DailySalesItemEntity item , SalesMstEntity salesMst) {

	    try {

	        String barcode   = item.getBarcode();       // 상품 바코드
	        Long locationId  = item.getLocationId();    // 매장 위치

	        int excelQty  = item.getQty();               // 엑셀 총 판매 수량


	        if (excelQty <= 0) {
	        	//수량 없음
	            return SalesResult.builder()
	                    .errorCode(1000)
	                    .build(); 
	        }

	        /** ---------------------------
	         *  가격 정보
	         * --------------------------- */
	        int unitPrice      = item.getUnitPrice(); // 상품 단가 - 엑셀에 있는 상품 단가
	        //할인된거 정책정으로 다 빼고 실 매출로 잡기로 함. 2026 04 24
	        int discountAmount = item.getDiscountAmount() == null ? 0 : item.getDiscountAmount(); // 총 할인금액 - 엑셀에 있는 총 할인금액

	        /** ---------------------------
	         *  상품 조회 (바코드 ) 88코드 - 해당 코드는 유니크 인덱스로 고유함.
	         * --------------------------- */
	        ProductMstEntity product = productMstRepository.findByPvarcodeNo(barcode).orElse(null);
	        if (product == null) {
	            return SalesResult.builder().errorCode(3).build(); // 바코드 없음
	        }
	        Long productId = product.getProductId();

	        /** ---------------------------
	         *  현재 재고 조회
	         * ---------------------------
	         * v_product_stock View
	         *
	         * ORG_STOCK_QTY
	         * - SALES_QTY
	         * - SPECIAL_QTY
	         *
	         * = AVAILABLE_QTY
	         */
	        ProductStockDto stock = summaryMapper.getProductStock(productId, locationId);

	        if (stock == null) {
	            return SalesResult.builder().errorCode(4).build(); // 재고 없음
	        }

	        /** ---------------------------
	         *  POS 오늘 판매량 조회 - 바코드 기준으로 조회
	         * ---------------------------
	         * sales_mst + sales_dtl 기준
	         * 오늘 판매된 수량
	         */
	        int todaySalesQty = summaryMapper.getTodaySalesQty(barcode, locationId, item.getSalesDate());

	        /**
	         * ---------------------------
	         *  추가 판매 수량 계산
	         * ---------------------------
	         * 집계 안잡힌 판매 = 엑셀 총 수량(반품 뺀 수량) - 이미 판매 완료된 POS 판매
	         */
	        int excelSaleQty = excelQty - todaySalesQty;
	        
	        /** ---------------------------
	         * 할인 계산 (공통)
	         * --------------------------- */
	        int perUnit = 0;
	        int remain  = 0;
	        if (discountAmount > 0) {
	            perUnit = discountAmount / excelQty;
	            remain  = discountAmount % excelQty;
	        }
	        
	        /** ---------------------------
	         * 신규 판매 없음 → 기존 할인만 적용
	         * --------------------------- */
	        // 할인 금액이 있을경우 sales잡힌 곳에 discountprice 나눠서 update 쳐줘야함.
	        // 그래야 엑셀 총액과 db 총액 맞음
	        //todaySalesQty 이게 존재하고 할인금액이 존재하면 차감 시켜주는 로직이 들어가야함. 2026 04 24
	        if (excelSaleQty <= 0) { 
	        	//해당 부분에서 예약된 수량 확인 후 할인 금액이 있으면 할인금액만큼 차감 해주는 로직 들어가야함.
	        	//예약 부분은 이미 sales에 들어간 상태라서 할인금액만 sales에 업데이트 쳐줌
	        	//임시 주석 : 이 부분은 예약시 누가 할인 받았는지 모르기때문에 일관분배 할인 로직이 안맞음. 그래서 주석처리
	        	if(discountAmount > 0) 
	        	{
	        		try {
	        			/** ---------------------------
	    	             * 기존 sales_dtl 조회
	    	             * --------------------------- */
	    	        	List<SalesDtlDto> existingList = summaryMapper.getSalesDtlList(barcode , locationId , item.getSalesDate());
	    	            
	    	            List<SalesDtlDto> mergeList = new ArrayList<>();
	    	            
	    	            int totalQty = existingList.stream()
	    	                    .mapToInt(SalesDtlDto::getQty)
	    	                    .sum();

    		            perUnit = discountAmount / totalQty;
    		            remain  = discountAmount % totalQty;
	    	            
	    	            /** ---------------------------
	    	             *  기존 판매 분배
	    	             * --------------------------- */
	    	            for (SalesDtlDto row : existingList) {
	    	                int qty = row.getQty();
	    	                int discount = perUnit * qty;
	    	                // remainder 분배 (앞에서부터)
	    	                if (remain > 0) {
	    	                    int add = Math.min(remain, qty);
	    	                    discount += add;
	    	                    remain   -= add;
	    	                }
	    	                row.setDiscountPrice(discount);
	    	                mergeList.add(row);
	    	            }
	    	            if (!mergeList.isEmpty()) {
	    	            	/** 할인금액 UPDATE */
	    	            	for(SalesDtlDto row :mergeList) {
	    	            		if (row.getSalesDtlId() == null) continue;
	    	            		summaryMapper.updateSalesDtlReservationDiscount(row.getSalesDtlId() , row.getDiscountPrice());
	    	            	}

	                        List<Long> salesIdList = mergeList.stream()
	                                .map(SalesDtlDto::getSalesId)
	                                .filter(id -> id != null)
	                                .distinct()
	                                .collect(Collectors.toList());
	                        
	                        if (!salesIdList.isEmpty()) {
	                        	/** MST 재계산 */
	                            summaryMapper.updateSalesMstReservationDiscount(salesIdList);
	                        }
	                    }
	        		}catch(Exception e) {
	        			e.printStackTrace();
	        			return SalesResult.builder()
	    	                    .errorCode(1) //
	    	                    .build();
	        		}
	        	}
	            return SalesResult.builder()
	                    .errorCode(100) // 판매 잡을 수량 없음. 이미 모두 잡혀있음.
	                    .build();
	        }

	        /** ---------------------------
	         * 재고 체크
	         * ---------------------------
	         * 현재 가용재고보다 많이 판매하려 하면 오류
	         */

	        int availableQty = stock.getAvailableQty();

	        if (availableQty < excelSaleQty) {
	            return SalesResult.builder()
	                    .errorCode(5)
	                    .abailableQty(availableQty)
	                    .build(); // 재고 부족
	        }

	        /** ---------------------------
	         * 판매 결과 누적 변수
	         * --------------------------- */
	        int totSaleQty      = 0;
	        int totalSalePrice  = 0;
	        int totalDiscount   = 0;

	        /** 할인 없는 경우 한번에 판매 처리 */
	        if (discountAmount <= 0) {

	            SalesDtlDto sdto = new SalesDtlDto();

	            sdto.setProductId(productId);
	            sdto.setQty(excelSaleQty); // 한번에 처리

	            sdto.setSalesType("VISIT");
	            sdto.setPaymentType("CARD");
	            
	            sdto.setUnitPrice(unitPrice);              // 단가
	            sdto.setQty(excelSaleQty);                 // 수량
	            sdto.setDiscountPrice(0);                  // 할인 없음
	            sdto.setOrgSalesPrice(unitPrice);
	            sdto.setSalesPrice(unitPrice * excelSaleQty); // 총금액

	            /** FIFO 재고 차감 판매 */
	            salesService.processSale( sdto, salesMst, locationId, "UNKNOWN" );

	            totSaleQty      = excelSaleQty;
	            totalSalePrice  = unitPrice * excelSaleQty;
	            totalDiscount   = 0;
	        }

	        /**  할인 있는 경우
	         * ---------------------------
	         * 할인금액 remainder 처리를 위해
	         * 1개씩 판매 처리
	         */
	        else {
	            /** ---------------------------
	             * 기존 sales_dtl 조회
	             * --------------------------- */
	            List<SalesDtlDto> existingList = summaryMapper.getSalesDtlList(barcode , locationId , item.getSalesDate());
	            List<SalesDtlDto> mergeList = new ArrayList<>();

	            
	            /** ---------------------------
	             * 기존 판매 분배
	             * --------------------------- */
	            for (SalesDtlDto row : existingList) {

	                int qty = row.getQty();
	                int discount = perUnit * qty;

	                // remainder 분배 (앞에서부터)
	                if (remain > 0) {
	                    int add = Math.min(remain, qty);
	                    discount += add;
	                    remain   -= add;
	                }
	                row.setDiscountPrice(discount);
	                mergeList.add(row);
	            }

	            /** ---------------------------
	             * 신규 판매
	             * --------------------------- */
	            for (int i = 0; i < excelSaleQty; i++) {
	                int discount = perUnit;
	                if (remain > 0) {
	                    discount += 1;
	                    remain--;
	                }
	                int salePrice = unitPrice - discount;
	                SalesDtlDto sdto = new SalesDtlDto();
	                
	                sdto.setProductId(productId);
	                sdto.setQty(1);

	                sdto.setSalesType("VISIT");
	                sdto.setPaymentType("CARD");

	                sdto.setUnitPrice(unitPrice);
	                sdto.setDiscountPrice(discount);
	                sdto.setOrgSalesPrice(unitPrice);

	                /** FIFO 판매 */
	                salesService.processSale(
	                        sdto,
	                        salesMst,
	                        locationId,
	                        "UNKNOWN"
	                );

	                totSaleQty     += 1;
	                totalSalePrice += salePrice;
	            }

	            /** ---------------------------
	             * 기존 할인 반영 (여기 위치 중요)
	             * --------------------------- */
	            if (!mergeList.isEmpty()) {
	            	/** 할인금액 UPDATE */
	            	for(SalesDtlDto row :mergeList) {
	            		if (row.getSalesDtlId() == null) continue;
	            		summaryMapper.updateSalesDtlReservationDiscount(row.getSalesDtlId() , row.getDiscountPrice());
	            	}

	                /** ---------------------------
	                 *  MST 재계산 (기존만)
	                 * --------------------------- */
	                List<Long> salesIdList = mergeList.stream()
	                        .map(SalesDtlDto::getSalesId)
	                        .filter(id -> id != null)
	                        .distinct()
	                        .collect(Collectors.toList());

	                if (!salesIdList.isEmpty()) {
	                    summaryMapper.updateSalesMstReservationDiscount(salesIdList);
	                }
	            }

	            /** ---------------------------
	             * 할인 총액은 엑셀 기준으로 고정
	             * --------------------------- */
	            totalDiscount = discountAmount;
	        }

	        /** ---------------------------
	         * 12. 결과 반환
	         * --------------------------- */
	        return SalesResult.builder()
	                .saleQty(totSaleQty)
	                .salesPrice(totalSalePrice)
	                .discountPrice(totalDiscount)
	                .build();

	    } catch (Exception e) {

	        e.printStackTrace();

	        return SalesResult.builder()
	                .errorCode(1)
	                .build(); // 알 수 없는 오류
	    }
	}
	
	private SalesMstEntity createVisitSalesMst(
			Long storeId,
	        String userId,
	        LocalDate salesDate
	) {

	    SalesMstEntity mst = SalesMstEntity.builder()
	    		.storeId(storeId)
	    		.userId(userId)
	    		.salesDate(salesDate.atStartOfDay())
	            .salesType(SalesType.VISIT)
	            .paymentType(PaymentType.CARD)
	            .salesStatus(SalesStatus.COMPLETE)
	            .createUser(userId)
	            .createDate(LocalDateTime.now())
	            .build();

	    return salesMstRepository.save(mst);
	}
	
	
	
	
	
	/**
	 * 반품 처리 (sales_dtl 기반)
	 */
	private void processReturn(DailySalesItemEntity item, SalesMstEntity salesMst) {
		
		

	    int refundQty    = item.getRefundQty() == null ? 0 : item.getRefundQty();
	    int refundAmount = item.getRefundAmount() == null ? 0 : item.getRefundAmount();

	    if (refundQty <= 0) return;

	    String barcode  = item.getBarcode();
	    Long locationId = item.getLocationId();

	    // 1. 상품 조회
	    ProductMstEntity product = productMstRepository.findByPvarcodeNo(barcode)
	            .orElseThrow(() -> new GlobalException(ResponseCode.NOT_FOUND, "상품 없음"));

	    Long productId = product.getProductId();

	    
	    System.out.println("salesId = " + salesMst.getSalesId());
		System.out.println("productId = " + productId);
	    // 2. sales_dtl 조회 (최근 생성된 것 기준)
	    List<SalesDtlEntity> salesDtlList =
	            salesDtlRepository.findBySalesIdAndProductIdOrderBySalesDtlIdDesc(
	                    salesMst.getSalesId(),
	                    productId
	            );
//	    List<SalesDtlEntity> salesDtlList = summaryMapper.selectSalesDtlForReturn( salesMst.getSalesId(), productId );

	    if (salesDtlList.isEmpty()) {
	        throw new GlobalException(ResponseCode.BAD_REQUEST, "반품할 판매 데이터 없음");
	    }

	    // 3. return_mst 생성
	    ReturnMstEntity returnMst = ReturnMstEntity.builder()
	            .salesId(salesMst.getSalesId())
	            .storeId(salesMst.getStoreId())
	            .locationId(locationId)
	            .returnDate(LocalDateTime.now())
	            .totalQty(refundQty)	
	            .totalAmount(refundAmount)
	            .status("COMPLETE")
	            .createUser("SYSTEM")
	            .build();

	    returnMstRepository.save(returnMst);

	    int remainQty = refundQty;
	    int lineNo = 1;

	    // 4. sales_dtl 기준으로 반품 처리 (역순)
	    for (SalesDtlEntity salesDtl : salesDtlList) {

	        if (remainQty <= 0) break;

	        int soldQty = salesDtl.getQty();

	        if (soldQty <= 0) continue;

	        int returnQty = Math.min(soldQty, remainQty);
	        //재고 복구는 안함.. 뷰에서 계산됨

	        // 🔥 return_dtl 생성
	        ReturnDtlEntity returnDtl = ReturnDtlEntity.builder()
	                .returnId(returnMst.getReturnId())
	                .salesDtlId(salesDtl.getSalesDtlId()) // 🔥 핵심
	                .productId(productId)
	                .productDtlId(salesDtl.getProductDtlId())
	                .lineNo(lineNo++)
	                .qty(returnQty)
	                .unitPrice(salesDtl.getUnitPrice())
	                .returnAmount(returnQty * salesDtl.getUnitPrice())
	                .createUser("SYSTEM")
	                .build();

	        returnDtlRepository.save(returnDtl);

	        remainQty -= returnQty;
	    }

	    if (remainQty > 0) {
	        throw new GlobalException(ResponseCode.BAD_REQUEST, "반품 수량 부족");
	    }
	}
	
    
}
