package com.technokratos.mapper;

import com.technokratos.dto.request.HallRequest;
import com.technokratos.dto.response.HallResponse;
import com.technokratos.model.Hall;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = RowMapper.class)
public interface HallMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "row", target = "rows")
    Hall toEntity(HallRequest request);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "rows", target = "row")
    HallResponse toResponse(Hall entity);
}
