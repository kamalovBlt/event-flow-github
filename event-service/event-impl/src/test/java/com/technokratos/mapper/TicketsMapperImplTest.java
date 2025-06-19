package com.technokratos.mapper;

import com.technokratos.dto.TicketCategoryDTO;
import com.technokratos.dto.request.ticket.TicketRequest;
import com.technokratos.dto.request.ticket.TicketsRequest;
import com.technokratos.dto.response.ticket.TicketResponse;
import com.technokratos.dto.response.ticket.TicketsResponse;
import com.technokratos.mapper.api.ticket.TicketMapper;
import com.technokratos.mapper.impl.ticket.TicketsMapperImpl;
import com.technokratos.model.Ticket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class TicketsMapperImplTest {

    @Mock
    private TicketMapper ticketMapper;

    @InjectMocks
    private TicketsMapperImpl ticketsMapper;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldMapTicketsListToResponseCorrectly() {
        Ticket ticket = new Ticket();
        ticket.setLocationId("LOC1");
        ticket.setHallId("HALL1");

        List<Ticket> tickets = List.of(ticket);
        TicketResponse ticketResponse = new TicketResponse(1L, 1L, 1L, true, BigDecimal.TEN);

        when(ticketMapper.toResponse(ticket)).thenReturn(ticketResponse);

        TicketsResponse response = ticketsMapper.toResponse(tickets);

        assertEquals("LOC1", response.locationId());
        assertEquals("HALL1", response.hallId());
        assertEquals(1, response.tickets().size());
        assertEquals(ticketResponse, response.tickets().get(0));
    }

    @Test
    void shouldMapRequestToTicketEntityListCorrectly() {
        TicketRequest request = new TicketRequest(1L, 1L, TicketCategoryDTO.COMMON, BigDecimal.valueOf(500));
        TicketsRequest ticketsRequest = new TicketsRequest("LOC1", "HALL1", List.of(request));

        Ticket mappedTicket = new Ticket();
        when(ticketMapper.toEntity(request)).thenReturn(mappedTicket);

        List<Ticket> result = ticketsMapper.toEntity(ticketsRequest);

        assertEquals(1, result.size());
        Ticket ticket = result.get(0);
        assertEquals("LOC1", ticket.getLocationId());
        assertEquals("HALL1", ticket.getHallId());
    }
}
