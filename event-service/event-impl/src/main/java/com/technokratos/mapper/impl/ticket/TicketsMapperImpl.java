package com.technokratos.mapper.impl.ticket;

import com.technokratos.dto.request.ticket.TicketsRequest;
import com.technokratos.dto.response.ticket.TicketsResponse;
import com.technokratos.mapper.api.ticket.TicketMapper;
import com.technokratos.mapper.api.ticket.TicketsMapper;
import com.technokratos.model.Ticket;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TicketsMapperImpl implements TicketsMapper {
    private final TicketMapper ticketMapper;

    @Override
    public TicketsResponse toResponse(List<Ticket> entities) {
        return new TicketsResponse(
                entities.get(0).getLocationId(),
                entities.get(0).getHallId(),
                entities.stream()
                        .map(ticketMapper::toResponse)
                        .toList()
        );
    }

    @Override
    public List<Ticket> toEntity(TicketsRequest request) {
        return request.tickets().stream()
                .map(ticketRequest -> {
                    Ticket ticket = ticketMapper.toEntity(ticketRequest);
                    ticket.setLocationId(request.locationId());
                    ticket.setHallId(request.hallId());
                    return ticket;
                })
                .toList();
    }
}
