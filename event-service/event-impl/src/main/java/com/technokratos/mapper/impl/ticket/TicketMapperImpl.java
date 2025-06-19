package com.technokratos.mapper.impl.ticket;

import com.technokratos.dto.request.ticket.TicketRequest;
import com.technokratos.dto.response.ticket.TicketResponse;
import com.technokratos.mapper.api.ticket.TicketCategoryMapper;
import com.technokratos.mapper.api.ticket.TicketMapper;
import com.technokratos.model.Ticket;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TicketMapperImpl implements TicketMapper {

    private final TicketCategoryMapper ticketCategoryMapper;

    @Override
    public TicketResponse toResponse(Ticket entity) {
        return new TicketResponse(
                entity.getId(),
                entity.getRowNum(),
                entity.getSeatNum(),
                entity.isSell(),
                entity.getCost()
        );
    }

    @Override
    public Ticket toEntity(TicketRequest request) {
        return Ticket.builder()
                .rowNum(request.rowNum())
                .seatNum(request.seatNum())
                .cost(request.cost())
                .ticketCategory(ticketCategoryMapper.toEntity(request.category()))
                .build();
    }
}
