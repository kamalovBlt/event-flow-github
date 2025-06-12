package com.technokratos.mapper;

import com.technokratos.dto.request.SeatRequest;
import com.technokratos.dto.response.SeatResponse;
import com.technokratos.model.Seat;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SeatMapper {
    Seat toEntity(SeatRequest request);
    SeatResponse toResponse(Seat entity);
}
