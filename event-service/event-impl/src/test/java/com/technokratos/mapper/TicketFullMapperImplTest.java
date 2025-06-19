package com.technokratos.mapper;

import com.technokratos.dto.TicketCategoryDTO;
import com.technokratos.dto.request.ticket.TicketFullRequest;
import com.technokratos.dto.response.ticket.TicketFullResponse;
import com.technokratos.mapper.api.ticket.TicketCategoryMapper;
import com.technokratos.mapper.impl.ticket.TicketFullMapperImpl;
import com.technokratos.model.Ticket;
import com.technokratos.model.TicketCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

class TicketFullMapperImplTest {

    @Mock
    private TicketCategoryMapper ticketCategoryMapper;
    @InjectMocks
    private TicketFullMapperImpl mapper;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldConvertTicketToFullResponse() {
        Ticket ticket = Ticket.builder()
                .id(1L)
                .locationId("loc1")
                .hallId("hall1")
                .rowNum(5L)
                .seatNum(10L)
                .isSell(true)
                .cost(BigDecimal.valueOf(500))
                .build();

        TicketFullResponse response = mapper.toResponse(ticket);

        assertEquals("loc1", response.locationId());
        assertEquals("hall1", response.hallId());
        assertEquals(1L, response.id());
        assertEquals(5L, response.rowNum());
        assertEquals(10L, response.seatNum());
        assertTrue(response.isSell());
        assertEquals(BigDecimal.valueOf(500), response.cost());
    }

    @Test
    void shouldConvertFullRequestToTicket() {
        TicketFullRequest request = new TicketFullRequest("loc1", "hall1", 5L, 10L, TicketCategoryDTO.VIP, BigDecimal.valueOf(500));
        when(ticketCategoryMapper.toEntity(TicketCategoryDTO.VIP)).thenReturn(TicketCategory.VIP);

        Ticket ticket = mapper.toEntity(request);

        assertEquals("loc1", ticket.getLocationId());
        assertEquals("hall1", ticket.getHallId());
        assertEquals(5L, ticket.getRowNum());
        assertEquals(10L, ticket.getSeatNum());
        assertEquals(BigDecimal.valueOf(500), ticket.getCost());
        assertEquals(TicketCategory.VIP, ticket.getTicketCategory());
    }
}