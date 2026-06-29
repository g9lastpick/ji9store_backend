package com.jjsoft.pos.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.jjsoft.pos.entity.ReviewEventCheckEntity;

public interface ReviewEventCheckRepository extends JpaRepository<ReviewEventCheckEntity, String> {

    /** 주어진 sub 목록 중 이벤트 참여(=true)로 체크된 userId 목록 */
    @Query("SELECT c.userId FROM ReviewEventCheckEntity c WHERE c.participated = true AND c.userId IN :userIds")
    List<String> findParticipatedUserIds(@Param("userIds") List<String> userIds);
}
