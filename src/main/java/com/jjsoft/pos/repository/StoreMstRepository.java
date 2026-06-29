package com.jjsoft.pos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jjsoft.pos.entity.StoreMstEntity;

@Repository
public interface StoreMstRepository extends JpaRepository<StoreMstEntity, Long> {}
