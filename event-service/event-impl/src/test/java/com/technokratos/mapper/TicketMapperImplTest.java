package com.technokratos.mapper;

import com.technokratos.dto.TicketCategoryDTO;
import com.technokratos.dto.request.ticket.TicketRequest;
import com.technokratos.dto.response.ticket.TicketResponse;
import com.technokratos.mapper.api.ticket.TicketCategoryMapper;
import com.technokratos.mapper.impl.ticket.TicketMapperImpl;
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

class TicketMapperImplTest {

    @Mock
    private TicketCategoryMapper ticketCategoryMapper;

    @InjectMocks
    private TicketMapperImpl ticketMapper;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldMapTicketToResponseCorrectly() {
        Ticket ticket = Ticket.builder()
                .id(1L)
                .rowNum(2L)
                .seatNum(3L)
                .cost(BigDecimal.valueOf(1200))
                .isSell(true)
                .build();

        TicketResponse response = ticketMapper.toResponse(ticket);

        assertEquals(1L, response.id());
        assertEquals(2L, response.rowNum());
        assertEquals(3L, response.seatNum());
        assertEquals(BigDecimal.valueOf(1200), response.cost());
        assertTrue(response.isSell());
    }

    @Test
    void shouldMapRequestToTicketEntityCorrectly() {
        TicketRequest request = new TicketRequest(2L, 3L, TicketCategoryDTO.VIP, BigDecimal.valueOf(1500));

        when(ticketCategoryMapper.toEntity(TicketCategoryDTO.VIP)).thenReturn(TicketCategory.VIP);

        Ticket ticket = ticketMapper.toEntity(request);

        assertEquals(2L, ticket.getRowNum());
        assertEquals(3L, ticket.getSeatNum());
        assertEquals(BigDecimal.valueOf(1500), ticket.getCost());
        assertEquals(TicketCategory.VIP, ticket.getTicketCategory());
    }
}
