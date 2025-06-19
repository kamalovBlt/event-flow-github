package com.technokratos.service;

import com.technokratos.dto.TicketCategoryDTO;
import com.technokratos.dto.request.EventRequest;
import com.technokratos.dto.request.ticket.TicketRequest;
import com.technokratos.dto.request.ticket.TicketsRequest;
import com.technokratos.dto.response.artist.ArtistShortResponse;
import com.technokratos.dto.response.event.EventResponse;
import com.technokratos.dto.response.ticket.TicketResponse;
import com.technokratos.exception.event.EventConflictException;
import com.technokratos.exception.event.EventNotFoundException;
import com.technokratos.exception.event.EventSaveException;
import com.technokratos.mapper.api.EventMapper;
import com.technokratos.model.Event;
import com.technokratos.model.Ticket;
import com.technokratos.repository.interfaces.EventRepository;
import com.technokratos.service.impl.EventServiceImpl;
import com.technokratos.service.interfaces.TicketService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

public class EventServiceImplTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private TicketService ticketService;

    @Mock
    private EventMapper eventMapper;

    @InjectMocks
    private EventServiceImpl eventService;

    private EventRequest eventRequest;
    private Event event;
    private EventResponse eventResponse;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        TicketResponse ticket = new TicketResponse(
                1L,
                1L,
                1L,
                false,
                BigDecimal.valueOf(1000)
        );

        eventRequest = new EventRequest(
                "Концерт",
                "Описание концерта",
                null,
                "loc123",
                "hall123",
                1L,
                LocalDateTime.of(2025, 4, 29, 18, 30),
                LocalDateTime.of(2025, 4, 29, 20, 30),
                new TicketsRequest("loc123","hall123",
                        List.of(new TicketRequest(2L,3L,
                                TicketCategoryDTO.VIP,BigDecimal.valueOf(1000)))),
                List.of()
        );

        event = new Event();
        event.setLocationId("loc123");
        event.setHallId("hall123");
        event.setStart(eventRequest.startTime());
        event.setEnd(eventRequest.endTime());

        eventResponse = new EventResponse(
                "Концерт",
                "Описание концерта",
                null,
                "loc123",
                "hall123",
                LocalDateTime.of(2025, 4, 29, 18, 30),
                LocalDateTime.of(2025, 4, 29, 20, 30),
                List.of(new ArtistShortResponse(2L,"ds","as","pasha")),
                List.of(1L)
        );
    }

    @Test
    void shouldSaveEventSuccessfully() {
        when(eventMapper.toEntity(eventRequest)).thenReturn(event);
        when(eventRepository.existsByTimeAndLocationIdAndHallId(
                event.getLocationId(), event.getHallId(), event.getStart(), event.getEnd()
        )).thenReturn(false);
        when(eventRepository.save(event)).thenReturn(Optional.of(1L));

        Long savedId = eventService.save(eventRequest);

        assertThat(savedId).isEqualTo(1L);
        verify(ticketService).batchSave(eventRequest.tickets());
        verify(eventRepository).save(event);
    }

    @Test
    void shouldThrowEventConflictExceptionWhenEventExists() {
        when(eventMapper.toEntity(eventRequest)).thenReturn(event);
        when(eventRepository.existsByTimeAndLocationIdAndHallId(
                event.getLocationId(), event.getHallId(), event.getStart(), event.getEnd()
        )).thenReturn(true);

        assertThatThrownBy(() -> eventService.save(eventRequest))
                .isInstanceOf(EventConflictException.class)
                .hasMessageContaining("Мероприятие в это время, в данной локации уже существует");

        verify(eventRepository, never()).save(any());
        verify(ticketService, never()).batchSave(any());
    }

    @Test
    void shouldThrowEventSaveExceptionWhenSaveFails() {
        when(eventMapper.toEntity(eventRequest)).thenReturn(event);
        when(eventRepository.existsByTimeAndLocationIdAndHallId(
                event.getLocationId(), event.getHallId(), event.getStart(), event.getEnd()
        )).thenReturn(false);
        when(eventRepository.save(event)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> eventService.save(eventRequest))
                .isInstanceOf(EventSaveException.class)
                .hasMessageContaining("Ошибка при сохранении");

        verify(ticketService, never()).batchSave(any());
    }

    @Test
    void shouldFindEventById() {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(eventMapper.toResponse(event)).thenReturn(eventResponse);

        EventResponse response = eventService.findById(1L);

        assertThat(response).isEqualTo(eventResponse);
        verify(eventRepository).findById(1L);
        verify(eventMapper).toResponse(event);
    }

    @Test
    void shouldThrowEventNotFoundWhenFindByIdFails() {
        when(eventRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> eventService.findById(1L))
                .isInstanceOf(EventNotFoundException.class)
                .hasMessageContaining("Событие с id 1 не найдено");
    }

    @Test
    void shouldFindAllEvents() {
        List<Event> events = List.of(event);
        List<EventResponse> responses = List.of(eventResponse);

        when(eventRepository.findAll(0, 10)).thenReturn(events);
        when(eventMapper.toResponse(event)).thenReturn(eventResponse);

        List<EventResponse> result = eventService.findAll(0, 10);

        assertThat(result).containsExactlyElementsOf(responses);
    }

    @Test
    void shouldUpdateEventSuccessfully() {
        when(eventRepository.existsById(1L)).thenReturn(true);
        when(eventMapper.toEntity(eventRequest)).thenReturn(event);
        event.setId(1L);
        when(eventRepository.existsByTimeAndLocationIdAndHallId(
                event.getLocationId(), event.getHallId(), event.getStart(), event.getEnd()
        )).thenReturn(false);

        eventService.update(1L, eventRequest);

        verify(eventRepository).update(event);
    }

    @Test
    void shouldThrowEventNotFoundWhenUpdateNonexistent() {
        when(eventRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> eventService.update(1L, eventRequest))
                .isInstanceOf(EventNotFoundException.class)
                .hasMessageContaining("Мероприятие для удаления не найдено");
    }

    @Test
    void shouldThrowEventConflictExceptionWhenUpdateConflict() {
        when(eventRepository.existsById(1L)).thenReturn(true);
        when(eventMapper.toEntity(eventRequest)).thenReturn(event);
        event.setId(1L);
        when(eventRepository.existsByTimeAndLocationIdAndHallId(
                event.getLocationId(), event.getHallId(), event.getStart(), event.getEnd()
        )).thenReturn(true);

        assertThatThrownBy(() -> eventService.update(1L, eventRequest))
                .isInstanceOf(EventConflictException.class)
                .hasMessageContaining("Мероприятие в это время, в данной локации уже существует");

        verify(eventRepository, never()).update(any());
    }

    @Test
    void shouldDeleteEventSuccessfully() {
        when(eventRepository.existsById(1L)).thenReturn(true);

        eventService.deleteById(1L);

        verify(eventRepository).deleteById(1L);
    }

    @Test
    void shouldThrowEventNotFoundWhenDeleteNonexistent() {
        when(eventRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> eventService.deleteById(1L))
                .isInstanceOf(EventNotFoundException.class)
                .hasMessageContaining("Мероприятие для удаления не найдено");
    }

    @Test
    void shouldReturnTrueIfUserIsCreator() {
        Authentication authentication = mock(Authentication.class);
        Jwt jwt = mock(Jwt.class);

        when(eventRepository.findCreatorIdByEventId(1L)).thenReturn(Optional.of(100L));
        when(authentication.getPrincipal()).thenReturn(jwt);
        when(jwt.getClaim("user-id")).thenReturn(100L);

        boolean result = eventService.isCreator(1L, authentication);

        assertThat(result).isTrue();
    }

    @Test
    void shouldReturnFalseIfUserIsNotCreator() {
        Authentication authentication = mock(Authentication.class);
        Jwt jwt = mock(Jwt.class);

        when(eventRepository.findCreatorIdByEventId(1L)).thenReturn(Optional.of(100L));
        when(authentication.getPrincipal()).thenReturn(jwt);
        when(jwt.getClaim("user-id")).thenReturn(200L);

        boolean result = eventService.isCreator(1L, authentication);

        assertThat(result).isFalse();
    }

    @Test
    void shouldThrowEventNotFoundWhenIsCreatorFails() {
        Authentication authentication = mock(Authentication.class);
        Jwt jwt = mock(Jwt.class);

        when(eventRepository.findCreatorIdByEventId(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> eventService.isCreator(1L, authentication))
                .isInstanceOf(EventNotFoundException.class)
                .hasMessageContaining("Мероприятие не найдено, id: 1");
    }
}
