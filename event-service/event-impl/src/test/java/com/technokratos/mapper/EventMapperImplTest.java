package com.technokratos.mapper;

import com.technokratos.dto.EventCategoryDTO;
import com.technokratos.dto.TicketCategoryDTO;
import com.technokratos.dto.request.EventRequest;
import com.technokratos.dto.request.ticket.TicketRequest;
import com.technokratos.dto.request.ticket.TicketsRequest;
import com.technokratos.dto.response.event.EventResponse;
import com.technokratos.mapper.api.EventCategoryMapper;
import com.technokratos.mapper.impl.EventMapperImpl;
import com.technokratos.model.Artist;
import com.technokratos.model.Event;
import com.technokratos.model.EventCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class EventMapperImplTest {

    @Mock
    private EventCategoryMapper eventCategoryMapper;

    @InjectMocks
    private EventMapperImpl eventMapper;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldMapEventToResponseWithArtistsCorrectly() {
        Artist artist = Artist.builder()
                .id(1L)
                .firstName("A")
                .lastName("B")
                .description("Desc")
                .build();

        Event event = Event.builder()
                .name("RockFest")
                .description("Annual Rock Festival")
                .eventCategory(EventCategory.CULTURAL)
                .locationId("LOC")
                .hallId("HALL")
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusHours(2))
                .artists(List.of(artist))
                .imageIds(List.of(1L,2L))
                .build();

        when(eventCategoryMapper.toResponse(EventCategory.CULTURAL)).thenReturn(EventCategoryDTO.CULTURAL);

        EventResponse response = eventMapper.toResponse(event);

        assertEquals("RockFest", response.name());
        assertEquals("Annual Rock Festival", response.description());
        assertEquals(EventCategoryDTO.CULTURAL, response.category());
        assertEquals("LOC", response.locationId());
        assertEquals("HALL", response.hallId());
        assertEquals(1, response.artists().size());
        assertEquals(List.of(1L,2L), response.imageIds());
    }

    @Test
    void shouldMapRequestToEventWithArtistsCorrectly() {
        EventRequest request = new EventRequest(
                "Jazz Night",
                "Smooth evening",
                EventCategoryDTO.CONCERT,
                "LOC",
                "HALL",
                2L,
                LocalDateTime.now(),
                LocalDateTime.now().plusHours(3),
                new TicketsRequest("LOC","HALL",
                        List.of(new TicketRequest(1L,2L,
                                TicketCategoryDTO.VIP, BigDecimal.valueOf(122L)))),
                List.of(1L,2L)
        );

        when(eventCategoryMapper.toEntity(EventCategoryDTO.CONCERT)).thenReturn(EventCategory.CONCERT);

        Event event = eventMapper.toEntity(request);

        assertEquals("Jazz Night", event.getName());
        assertEquals("Smooth evening", event.getDescription());
        assertEquals(EventCategory.CONCERT, event.getEventCategory());
        assertEquals("LOC", event.getLocationId());
        assertEquals("HALL", event.getHallId());
        assertEquals(2L, event.getCreatorId());
        assertEquals(2, event.getArtists().size());
        assertEquals(1L, event.getArtists().get(0).getId());
    }
}
