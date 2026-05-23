package com.jjsoft.pos.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jjsoft.pos.entity.ReviewReactionEntity;

@Repository
public interface ReviewReactionRepository extends JpaRepository<ReviewReactionEntity, Long> {

    Optional<ReviewReactionEntity> findByReviewIdAndUserIdAndReactionType(
            Long reviewId, String userId, String reactionType);

    List<ReviewReactionEntity> findByReviewIdInAndUserIdAndReactionType(
            List<Long> reviewIds, String userId, String reactionType);
}
