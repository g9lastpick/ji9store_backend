package com.jjsoft.pos.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jjsoft.pos.entity.StoreMstEntity;

@Repository
public interface StoreMstRepository extends JpaRepository<StoreMstEntity, Long> {

    /** 사용중인 점포 목록(정렬순). 모바일 최초 가입 점포 선택 화면용. */
    List<StoreMstEntity> findByUseYnOrderBySortOrderAsc(String useYn);
}
