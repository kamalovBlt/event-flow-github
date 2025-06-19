package com.technokratos.mapper.impl.ticket;

import com.technokratos.dto.request.ticket.TicketFullRequest;
import com.technokratos.dto.response.ticket.TicketFullResponse;
import com.technokratos.mapper.api.ticket.TicketCategoryMapper;
import com.technokratos.mapper.api.ticket.TicketFullMapper;
import com.technokratos.model.Ticket;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TicketFullMapperImpl implements TicketFullMapper {

    private final TicketCategoryMapper ticketCategoryMapper;

    @Override
    public TicketFullResponse toResponse(Ticket entity) {
        return new TicketFullResponse(
                entity.getLocationId(),
                entity.getHallId(),
                entity.getId(),
                entity.getRowNum(),
                entity.getSeatNum(),
                entity.isSell(),
                entity.getCost()
        );
    }

    @Override
    public Ticket toEntity(TicketFullRequest request) {
        return Ticket.builder()
                .locationId(request.locationId())
                .hallId(request.hallId())
                .rowNum(request.rowNum())
                .seatNum(request.seatNum())
                .cost(request.cost())
                .ticketCategory(ticketCategoryMapper.toEntity(request.category()))
                .build();
    }
}
