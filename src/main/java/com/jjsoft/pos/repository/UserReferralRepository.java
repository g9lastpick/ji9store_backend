package com.jjsoft.pos.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jjsoft.pos.entity.UserReferralEntity;

public interface UserReferralRepository  extends JpaRepository<UserReferralEntity, Long> {

    /**
     * 유저 기준 추천인 정보 조회
     */
	Optional<UserReferralEntity> findByUserId(String userId);
	
	boolean existsByUserId(String userId);

}
