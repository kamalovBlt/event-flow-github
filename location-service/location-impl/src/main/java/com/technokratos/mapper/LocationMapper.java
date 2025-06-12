package com.technokratos.mapper;

import com.technokratos.dto.request.LocationRequest;
import com.technokratos.dto.response.LocationResponse;
import com.technokratos.dto.response.LocationShortResponse;
import com.technokratos.model.Location;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {AddressMapper.class, HallMapper.class})
public interface LocationMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "addressDto", target = "address")
    @Mapping(source = "hall", target = "halls")
    Location toEntity(LocationRequest request);

    @Mapping(source = "address", target = "address")
    @Mapping(source = "halls", target = "hall")
    LocationResponse toResponse(Location entity);

    @Mapping(source = "address", target = "address")
    LocationShortResponse toShortResponse(Location entity);
}
