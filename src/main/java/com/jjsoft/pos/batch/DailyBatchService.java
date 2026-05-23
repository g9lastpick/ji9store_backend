package com.jjsoft.pos.batch;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jjsoft.pos.dto.biztalk.BiztalkRequestDto;
import com.jjsoft.pos.dto.biztalk.BiztalkRequestDto.Attach;
import com.jjsoft.pos.dto.biztalk.BiztalkRequestDto.BiztalkButton;
import com.jjsoft.pos.entity.BatchLogEntity;
import com.jjsoft.pos.entity.SpecialDtlEntity;
import com.jjsoft.pos.mapper.BatchMapper;
import com.jjsoft.pos.repository.BatchLogRepository;
import com.jjsoft.pos.repository.SpecialDtlRepository;
import com.jjsoft.pos.service.NotificationService;
import com.jjsoft.pos.service.common.ImageManagerService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class DailyBatchService {

	
	private final BatchMapper batchMapper;
	private final BatchLogRepository batchLogRepository;
	private final ImageManagerService imageManagerService;
	private final SpecialDtlRepository specialDtlRepository;

	private final NotificationService notificationService;
	
	
	
	
	@Transactional
//	@Scheduled(cron = "0 */1 * * * *")  // 디버깅용 2분
	@Scheduled(cron = "0 30 23 * * *") // 매일 밤 23:30
    public void runReservationComplete() {
		
      BatchLogEntity bLog = null;
      try {
    	  
	      	bLog = BatchLogEntity.builder()
	      		    .batchName("Reservation Cancel")
	      		    .startTime(LocalDateTime.now())
	      		    .status("RUNNING")
	      		    .createUser("SYSTEM")
	      		    .build();
	
	      	batchLogRepository.save(bLog); // INSERT
	      	
      		String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        	batchMapper.cancelTodayNoVisitReservations(today);
        	batchMapper.stopTodaySpecialMst(today);
	      	
	      	bLog.setEndTime(LocalDateTime.now());
	      	bLog.setStatus("SUCCESS");
	      	bLog.setResultMsg(" 금일 미방문 예약 자동 취소 완료");
	      	bLog.setExecutionTime(ChronoUnit.SECONDS.between(bLog.getStartTime(), bLog.getEndTime()));
	      	batchLogRepository.save(bLog); // UPDATE
	      	
	      	
		} catch (Exception e) {
			// TODO: handle exception
			if(bLog != null) {
				bLog.setEndTime(LocalDateTime.now());
				bLog.setStatus("FAIL");
				bLog.setResultMsg("에러 발생");
				bLog.setErrorStack(e.getMessage()); // Apache Commons Lang 사용 가능
				bLog.setExecutionTime(ChronoUnit.SECONDS.between(bLog.getStartTime(), bLog.getEndTime()));
				batchLogRepository.save(bLog);
			}
		}
		
    }
	
	/** 특가 이미지 삭제 */
	@Scheduled(cron = "0 40 23 * * *") // 매일 밤 23:40
    public void runSpecialImageCleanup() {
		
      BatchLogEntity bLog = null;
      try {
    	  
	      	bLog = BatchLogEntity.builder()
	      		    .batchName("특가 이미지 삭제 배치")
	      		    .startTime(LocalDateTime.now())
	      		    .status("RUNNING")
	      		    .createUser("SYSTEM")
	      		    .build();
	
	      	batchLogRepository.save(bLog); // INSERT
	      //특가 이미지 삭제
	      	List<SpecialDtlEntity> expiredList = specialDtlRepository.findExpiredWithImage();
	      	for (SpecialDtlEntity dtl : expiredList) {
	            try {
	                imageManagerService.deleteSpecialDtlImageById(dtl.getSpecialDtlId());
	            } catch (Exception e) {
	                // 삭제 실패 시 로그 남기고 넘어감
	            }
	        }
	      	
	      	bLog.setEndTime(LocalDateTime.now());
	      	bLog.setStatus("SUCCESS");
	      	bLog.setResultMsg(" 특가 썸네일 이미지 삭제 완료");
	      	bLog.setExecutionTime(ChronoUnit.SECONDS.between(bLog.getStartTime(), bLog.getEndTime()));
	      	batchLogRepository.save(bLog); // UPDATE
	      	
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			if(bLog != null) {
				bLog.setEndTime(LocalDateTime.now());
				bLog.setStatus("FAIL");
				bLog.setResultMsg("에러 발생");
				bLog.setErrorStack(e.getMessage()); // Apache Commons Lang 사용 가능
				bLog.setExecutionTime(ChronoUnit.SECONDS.between(bLog.getStartTime(), bLog.getEndTime()));
				batchLogRepository.save(bLog);
			}
		}
		
    }
	
	
	
	/** 
     * 매일 18시에 오늘 픽업 대상자에게 알림톡 발송
     */
    @Scheduled(cron = "0 0 18 * * *") // 매일 18:00
    @Transactional
    public void sendPickupReminders() {
    	BatchLogEntity bLog = null;
        try {
      	  
  	      	bLog = BatchLogEntity.builder()
  	      		    .batchName("batch alim talk send")
  	      		    .startTime(LocalDateTime.now())
  	      		    .status("RUNNING")
  	      		    .createUser("SYSTEM")
  	      		    .build();
  	      	batchLogRepository.save(bLog); // INSERT
  	      	
        	if(notificationService.sendAllAlimtalk())//배치 알림톡 실행 
        	{
        		bLog.setEndTime(LocalDateTime.now());
      	      	bLog.setStatus("SUCCESS");
      	      	bLog.setResultMsg("매일 6시 픽업 배치 전송 완료");
      	      	bLog.setExecutionTime(ChronoUnit.SECONDS.between(bLog.getStartTime(), bLog.getEndTime()));
      	      	batchLogRepository.save(bLog); // UPDATE
        	}else {
        		bLog.setEndTime(LocalDateTime.now());
      	      	bLog.setStatus("FAILED");
      	      	bLog.setResultMsg(" 매일 6시 픽업 배치 전송 실패");
      	      	bLog.setExecutionTime(ChronoUnit.SECONDS.between(bLog.getStartTime(), bLog.getEndTime()));
      	      	batchLogRepository.save(bLog); // UPDATE
        	}
  		} catch (Exception e) {
  			// TODO: handle exception
  			if(bLog != null) {
  				bLog.setEndTime(LocalDateTime.now());
  				bLog.setStatus("FAIL");
  				bLog.setResultMsg("에러 발생");
  				bLog.setErrorStack(e.getMessage()); // Apache Commons Lang 사용 가능
  				bLog.setExecutionTime(ChronoUnit.SECONDS.between(bLog.getStartTime(), bLog.getEndTime()));
  				batchLogRepository.save(bLog);
  			}
  		}
    }
    
    /**
     * 📆 매일 새벽 3시마다 실행
     * 1개월 지난 비즈톡 전송 로그 삭제
     * 정책 확인 후 시간 및 로그 삭제 범위 변경해야함.
     */
    @Transactional
    @Scheduled(cron = "0 0 3 * * *")  // 매일 03:00 실행
    public void deleteOldBiztalkLogs() {
        BatchLogEntity bLog = null;
        try {
      	  
  	      	bLog = BatchLogEntity.builder()
  	      		    .batchName ("batch deleteOldBiztalkLogs")
  	      		    .startTime (LocalDateTime.now())
  	      		    .status    ("RUNNING")
  	      		    .createUser("BATCH")
  	      		    .build();
  	      	batchLogRepository.save(bLog); // INSERT
  	      	int deletedCount = batchMapper.deleteOldSendLogs();
  	      	
  	      	bLog.setEndTime(LocalDateTime.now());
	      	bLog.setStatus("SUCCESS");
	      	bLog.setResultMsg("매일 새벽 3시 알림톡 1개월 전 로그 삭제 삭제건수 : " + deletedCount);
	      	bLog.setExecutionTime(ChronoUnit.SECONDS.between(bLog.getStartTime(), bLog.getEndTime()));
	      	batchLogRepository.save(bLog); // UPDATE
  		} catch (Exception e) {
  			// TODO: handle exception
  			if(bLog != null) {
  				bLog.setEndTime(LocalDateTime.now());
  				bLog.setStatus("FAIL");
  				bLog.setResultMsg("매일 새벽 3시 알림톡 1개월 전 로그 삭제 에러 발생");
  				bLog.setErrorStack(e.getMessage());
  				bLog.setExecutionTime(ChronoUnit.SECONDS.between(bLog.getStartTime(), bLog.getEndTime()));
  				batchLogRepository.save(bLog);
  			}
  		}
    }
	
	
	
//    @Transactional
//	@Scheduled(cron = "0 */2 * * * *")  // 디버깅용 2분
//	@Scheduled(cron = "0 0 23 * * *") // 매일 밤 23시
//    public void runDiscountPolicyBatch() {
//        log.info("🔁 할인 정책 배치 시작");
//        BatchLogEntity bLog = null;
//        try {
//        	bLog = BatchLogEntity.builder()
//        		    .batchName("Product Policy Batch")
//        		    .startTime(LocalDateTime.now())
//        		    .status("RUNNING")
//        		    .createUser("SYSTEM")
//        		    .build();
//
//        		batchLogRepository.save(bLog); // INSERT
//        	
//        	
//        	batchMapper.resetOriginalPrice();
//        	batchMapper.applyDiscountPolicy();
//        	
//        	bLog.setEndTime(LocalDateTime.now());
//        	bLog.setStatus("SUCCESS");
//        	bLog.setResultMsg("상품 정책 판매가 변환 완료");
//        	bLog.setExecutionTime(ChronoUnit.SECONDS.between(bLog.getStartTime(), bLog.getEndTime()));
//        	batchLogRepository.save(bLog); // UPDATE
//			
//		} catch (Exception e) {
//			// TODO: handle exception
//			log.info("✅ 할인 정책 배치 에러");
//			e.printStackTrace();
//			if(bLog != null) {
//				bLog.setEndTime(LocalDateTime.now());
//				bLog.setStatus("FAIL");
//				bLog.setResultMsg("에러 발생");
//				bLog.setErrorStack(e.getMessage()); // Apache Commons Lang 사용 가능
//				bLog.setExecutionTime(ChronoUnit.SECONDS.between(bLog.getStartTime(), bLog.getEndTime()));
//				batchLogRepository.save(bLog);
//			}
//		}
//        log.info("✅ 할인 정책 배치 완료");
//    }
}
