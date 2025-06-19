package com.technokratos.mapper.api;

/**
 * @param <RQ> Request DTO
 * @param <E> Entity
 * @param <RS> Response DTO
 */
public interface Mapper<RQ, E, RS> {
    RS toResponse(E entity);
    E toEntity(RQ request);
}

