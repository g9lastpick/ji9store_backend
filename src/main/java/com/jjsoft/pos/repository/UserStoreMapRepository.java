package com.jjsoft.pos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jjsoft.pos.entity.UserStoreMapEntity;
import com.jjsoft.pos.entity.UserStoreMapId;

@Repository
public interface UserStoreMapRepository extends JpaRepository<UserStoreMapEntity, UserStoreMapId> {}
