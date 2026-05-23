package com.jjsoft.pos.repository;

import com.jjsoft.pos.entity.TemplateMstEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 비즈톡 템플릿 마스터 Repository
 */
@Repository
public interface TemplateMstRepository extends JpaRepository<TemplateMstEntity, Long> {

    /**
     * 템플릿 코드로 단건 조회
     * @param tmplCode 템플릿 코드
     * @return TemplateMstEntity
     */
    Optional<TemplateMstEntity> findByTmplCode(String tmplCode);

}
