package com.jjsoft.pos.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.jjsoft.pos.entity.UserMstEntity;

@Repository
public interface UserMstRepository extends JpaRepository<UserMstEntity, Long> {
	
	@Query("SELECT u FROM UserMstEntity u WHERE u.userId = :userId")
    Optional<UserMstEntity> getUserByUserId(@Param("userId") String userId); 
	
	List<UserMstEntity> findByUserIdContaining(String userId);
	
	List<UserMstEntity> findByUserIdContainingOrderByCreateDateDesc(String userId);
	
	
	@Query("SELECT u " +
	           "FROM UserMstEntity u " +
	           "WHERE (:keyword IS NULL OR :keyword = '' " +
	           "   OR u.userId  LIKE %:keyword% " +
	           "   OR u.name    LIKE %:keyword% " +
	           "   OR u.email   LIKE %:keyword% " +
	           "   OR u.phone   LIKE %:keyword%) " +
	           "ORDER BY u.createDate DESC")
	    List<UserMstEntity> searchUsers(@Param("keyword") String keyword);
	
	
	//전화번호로 유저검색
	Optional<UserMstEntity> findFirstByPhoneEndingWith(String phone);

	List<UserMstEntity> findByPhoneEndingWith(String phone);

	//어드민 리뷰 목록 — userId 묶음으로 유저 일괄 조회
	List<UserMstEntity> findByUserIdIn(List<String> userIds);
}
