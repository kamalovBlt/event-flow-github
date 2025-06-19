package com.technokratos.service;

import com.technokratos.dto.TicketCategoryDTO;
import com.technokratos.dto.request.ticket.TicketFullRequest;
import com.technokratos.dto.request.ticket.TicketRequest;
import com.technokratos.dto.request.ticket.TicketsRequest;
import com.technokratos.dto.response.ticket.TicketFullResponse;
import com.technokratos.dto.response.ticket.TicketResponse;
import com.technokratos.dto.response.ticket.TicketsResponse;
import com.technokratos.exception.event.EventNotFoundException;
import com.technokratos.exception.ticket.TicketNotFoundException;
import com.technokratos.exception.ticket.TicketSaveException;
import com.technokratos.mapper.api.ticket.TicketFullMapper;
import com.technokratos.mapper.api.ticket.TicketsMapper;
import com.technokratos.model.Ticket;
import com.technokratos.model.TicketCategory;
import com.technokratos.repository.interfaces.EventRepository;
import com.technokratos.repository.interfaces.TicketRepository;
import com.technokratos.service.impl.TicketServiceImpl;
import com.technokratos.service.interfaces.TicketService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TicketServiceImplTest {

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private TicketFullMapper ticketFullMapper;

    @Mock
    private TicketsMapper ticketsMapper;

    @InjectMocks
    private TicketServiceImpl ticketService;

    private TicketFullRequest ticketFullRequest;
    private TicketFullResponse ticketFullResponse;
    private TicketRequest ticketRequest;
    private TicketResponse ticketResponse;
    private Ticket ticket;
    private TicketsRequest ticketsRequest;
    private List<Ticket> ticketList;
    private TicketsResponse ticketsResponse;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        ticketFullRequest = new TicketFullRequest(
            "1L",
            "2L",
            1L,
            2L,
            TicketCategoryDTO.VIP,
            BigDecimal.valueOf(1000)
        );

        ticket = new Ticket();
        ticket.setId(10L);
        ticket.setEventId(1L);
        ticket.setLocationId("2L");
        ticket.setHallId("2L");
        ticket.setTicketCategory(TicketCategory.VIP);
        ticket.setCost(BigDecimal.valueOf(1000));
        ticket.setSell(false);

        ticketFullResponse = new TicketFullResponse(
            "10L",
            "1L",
            2L,
            1L,
            1L,
            false,
                BigDecimal.valueOf(1000)
        );

        ticketRequest = new TicketRequest(
                1L,
                2L,
                TicketCategoryDTO.VIP,
                BigDecimal.valueOf(1000)
        );

        ticketResponse = new TicketResponse(
                12L,
                1L,
                2L,
                true,
                BigDecimal.valueOf(1000)
        );

        ticketsRequest = new TicketsRequest("10L", "1L", List.of(ticketRequest));
        ticketList = List.of(ticket);

        ticketsResponse = new TicketsResponse("10L", "1L",List.of(ticketResponse));
    }

    @Test
    void shouldSaveTicketSuccessfully() {
        when(eventRepository.existsById(1L)).thenReturn(true);
        when(ticketFullMapper.toEntity(ticketFullRequest)).thenReturn(ticket);
        when(ticketRepository.save(1L, ticket)).thenReturn(Optional.of(10L));

        Long savedId = ticketService.save(1L, ticketFullRequest);

        assertThat(savedId).isEqualTo(10L);
        verify(ticketRepository).save(1L, ticket);
    }

    @Test
    void shouldThrowEventNotFoundWhenSaveWithInvalidEvent() {
        when(eventRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> ticketService.save(1L, ticketFullRequest))
                .isInstanceOf(EventNotFoundException.class)
                .hasMessageContaining("Не найдено мероприятие для сохранения билета");

        verify(ticketRepository, never()).save(anyLong(), any());
    }

    @Test
    void shouldThrowTicketSaveExceptionWhenSaveFails() {
        when(eventRepository.existsById(1L)).thenReturn(true);
        when(ticketFullMapper.toEntity(ticketFullRequest)).thenReturn(ticket);
        when(ticketRepository.save(1L, ticket)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> ticketService.save(1L, ticketFullRequest))
                .isInstanceOf(TicketSaveException.class)
                .hasMessageContaining("Ошибка сохранения билета");

        verify(ticketRepository).save(1L, ticket);
    }

    @Test
    void shouldBatchSaveTickets() {
        when(ticketsMapper.toEntity(ticketsRequest)).thenReturn(ticketList);

        ticketService.batchSave(ticketsRequest);

        verify(ticketRepository).batchSave(ticketList);
    }

    @Test
    void shouldFindTicketByIdSuccessfully() {
        when(eventRepository.existsById(1L)).thenReturn(true);
        when(ticketRepository.findById(1L, 10L)).thenReturn(Optional.of(ticket));
        when(ticketFullMapper.toResponse(ticket)).thenReturn(ticketFullResponse);

        TicketFullResponse response = ticketService.findById(1L, 10L);

        assertThat(response).isEqualTo(ticketFullResponse);
        verify(ticketRepository).findById(1L, 10L);
    }

    @Test
    void shouldThrowEventNotFoundWhenFindTicketByIdInvalidEvent() {
        when(eventRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> ticketService.findById(1L, 10L))
                .isInstanceOf(EventNotFoundException.class)
                .hasMessageContaining("Мероприятие не найдено");

        verify(ticketRepository, never()).findById(anyLong(), anyLong());
    }

    @Test
    void shouldThrowTicketNotFoundWhenFindTicketByIdInvalidTicket() {
        when(eventRepository.existsById(1L)).thenReturn(true);
        when(ticketRepository.findById(1L, 10L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> ticketService.findById(1L, 10L))
                .isInstanceOf(TicketNotFoundException.class)
                .hasMessageContaining("Билет на данное мероприятие не найден");
    }

    @Test
    void shouldFindAllTicketsByEventId() {
        when(eventRepository.existsById(1L)).thenReturn(true);
        when(ticketRepository.findAllByEventId(1L)).thenReturn(ticketList);
        when(ticketsMapper.toResponse(ticketList)).thenReturn(ticketsResponse);

        TicketsResponse response = ticketService.findAllByEventId(1L);

        assertThat(response).isEqualTo(ticketsResponse);
        verify(ticketRepository).findAllByEventId(1L);
    }

    @Test
    void shouldThrowEventNotFoundWhenFindAllByInvalidEvent() {
        when(eventRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> ticketService.findAllByEventId(1L))
                .isInstanceOf(EventNotFoundException.class)
                .hasMessageContaining("Мероприятие не найдено");

        verify(ticketRepository, never()).findAllByEventId(anyLong());
    }

    @Test
    void shouldUpdateTicketSuccessfully() {
        when(eventRepository.existsById(1L)).thenReturn(true);
        when(ticketRepository.existsById(1L, 10L)).thenReturn(true);
        when(ticketFullMapper.toEntity(ticketFullRequest)).thenReturn(ticket);

        ticketService.update(1L, 10L, ticketFullRequest);

        verify(ticketRepository).update(1L, 10L, ticket);
    }

    @Test
    void shouldThrowEventNotFoundWhenUpdateInvalidEvent() {
        when(eventRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> ticketService.update(1L, 10L, ticketFullRequest))
                .isInstanceOf(EventNotFoundException.class)
                .hasMessageContaining("Мероприятие не найдено");

        verify(ticketRepository, never()).update(anyLong(), anyLong(), any());
    }

    @Test
    void shouldThrowTicketNotFoundWhenUpdateInvalidTicket() {
        when(eventRepository.existsById(1L)).thenReturn(true);
        when(ticketRepository.existsById(1L, 10L)).thenReturn(false);

        assertThatThrownBy(() -> ticketService.update(1L, 10L, ticketFullRequest))
                .isInstanceOf(TicketNotFoundException.class)
                .hasMessageContaining("Билет для обновления не найден");

        verify(ticketRepository, never()).update(anyLong(), anyLong(), any());
    }

    @Test
    void shouldDeleteTicketSuccessfully() {
        when(eventRepository.existsById(1L)).thenReturn(true);
        when(ticketRepository.existsById(1L, 10L)).thenReturn(true);

        ticketService.deleteById(1L, 10L);

        verify(ticketRepository).deleteById(1L, 10L);
    }

    @Test
    void shouldThrowEventNotFoundWhenDeleteInvalidEvent() {
        when(eventRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> ticketService.deleteById(1L, 10L))
                .isInstanceOf(EventNotFoundException.class)
                .hasMessageContaining("Мероприятие не найдено");

        verify(ticketRepository, never()).deleteById(anyLong(), anyLong());
    }

    @Test
    void shouldThrowTicketNotFoundWhenDeleteInvalidTicket() {
        when(eventRepository.existsById(1L)).thenReturn(true);
        when(ticketRepository.existsById(1L, 10L)).thenReturn(false);

        assertThatThrownBy(() -> ticketService.deleteById(1L, 10L))
                .isInstanceOf(TicketNotFoundException.class)
                .hasMessageContaining("Билет для удаления не найден");

        verify(ticketRepository, never()).deleteById(anyLong(), anyLong());
    }
}
