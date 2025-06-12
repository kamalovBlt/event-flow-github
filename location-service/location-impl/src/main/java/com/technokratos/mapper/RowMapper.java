package com.technokratos.mapper;

import com.technokratos.dto.request.RowRequest;
import com.technokratos.dto.response.RowResponse;
import com.technokratos.model.Row;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = SeatMapper.class)
public interface RowMapper {

    @Mapping(source = "seat", target = "seats")
    Row toEntity(RowRequest request);

    @Mapping(source = "seats", target = "seat")
    RowResponse toResponse(Row entity);
}

