package com.jjsoft.pos.service;

import java.util.List;

/**
 * 조회 전용 서비스 공통 인터페이스
 * @param <T> 반환 DTO 타입
 * @param <C> 조건 DTO 타입
 */
public interface QueryService<T,C> {
	public List<T> getDataList(C condition);
}
