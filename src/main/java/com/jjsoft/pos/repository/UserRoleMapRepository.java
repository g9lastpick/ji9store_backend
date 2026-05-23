package com.jjsoft.pos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jjsoft.pos.entity.UserRoleMapEntity;
import com.jjsoft.pos.entity.UserRoleMapId;

@Repository
public interface UserRoleMapRepository extends JpaRepository<UserRoleMapEntity, UserRoleMapId> {}
