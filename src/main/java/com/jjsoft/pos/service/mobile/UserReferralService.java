package com.jjsoft.pos.service.mobile;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jjsoft.pos.dto.mobile.ReferralResponseDto;
import com.jjsoft.pos.dto.mobile.ReferralSaveRequest;
import com.jjsoft.pos.entity.UserMstEntity;
import com.jjsoft.pos.entity.UserReferralEntity;
import com.jjsoft.pos.mapper.MobileReferralMapper;
import com.jjsoft.pos.repository.UserMstRepository;
import com.jjsoft.pos.repository.UserReferralRepository;

import lombok.RequiredArgsConstructor;

/**
 * 추천인 서비스
 */
@Service
@RequiredArgsConstructor
public class UserReferralService {

	private final UserReferralRepository userReferralRepository;
    private final UserMstRepository userMstRepository;
    private final MobileReferralMapper mobileReferralMapper;

    
    public boolean isTodayJoin(String userId){

        Optional<UserMstEntity> user = userMstRepository.getUserByUserId(userId);

        if(user.isEmpty()){
            return false;
        }

        // 오늘 가입 여부
        LocalDate today = LocalDate.now();

        boolean isTodayJoin = user.get()
                .getCreateDate()
                .toLocalDate()
                .isEqual(today);

        if(!isTodayJoin){
            return false;
        }

        // 추천 정보 존재 여부
        boolean referralExists = userReferralRepository.existsByUserId(userId);

        // 추천 정보 있으면 팝업 안뜸
        return !referralExists;
    }
    
    
    /**
     * 추천인 저장
     */
    @Transactional
    public boolean saveReferral(ReferralSaveRequest req) {

    	try {
    		String referrerUserId = null;
    		String referrerUserNm = null;

            if ("N".equals(req.getNoneYn()) && req.getReferrerPhone() != null) {

                String normalizedPhone = normalizePhone(req.getReferrerPhone());

                Optional<UserMstEntity> ent =
                        userMstRepository.findFirstByPhoneEndingWith(normalizedPhone);

                if (ent.isPresent()) {
                    referrerUserId = ent.get().getUserId();
                    referrerUserNm = ent.get().getName();
                }
            }
            
            Optional<UserMstEntity> my =
                    userMstRepository.getUserByUserId(req.getUserId());
            String userNm = null;
            if (my.isPresent()) {
            	userNm = my.get().getName();
            }

            UserReferralEntity entity =
                    UserReferralEntity.builder()
                            .userId        (req.getUserId())
                            .userNm        (userNm)
                            .referrerUserId(referrerUserId)
                            .referrerUserNm(referrerUserNm)
                            .referrerPhone (req.getReferrerPhone())
                            .noneYn        (req.getNoneYn())
                            .createUser    (req.getUserId())
                            .build();

            userReferralRepository.saveAndFlush(entity);
            
            return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
        
    }
    
    /**
     * 전화번호 normalize
     * 010-1234-5678 -> 10-1234-5678
     */
    private String normalizePhone(String phone){

        if(phone == null || phone.isEmpty()) return null;

        return phone.replaceFirst("^010", "10");
    }
    
    
    
    
    
    /* 추천인 정보 조회 */
    public ReferralResponseDto getReferralInfo(String userId) {

    	try {
    		return mobileReferralMapper.getReferralInfo(userId);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return ReferralResponseDto.builder().name("").phone("").referralCount(0).build();
        
    }
}
