package com.jjsoft.pos.service;

public interface WriteService<T> {
    void register(T dto);
    void update(T dto);
    void delete(Long id);
}
