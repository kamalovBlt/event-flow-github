package com.technokratos.mapper;

import com.technokratos.dto.EventCategoryDTO;
import com.technokratos.dto.request.ArtistRequest;
import com.technokratos.dto.response.artist.ArtistResponse;
import com.technokratos.mapper.api.EventCategoryMapper;
import com.technokratos.mapper.impl.ArtistMapperImpl;
import com.technokratos.model.Artist;
import com.technokratos.model.Event;
import com.technokratos.model.EventCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class ArtistMapperImplTest {

    @Mock
    private EventCategoryMapper eventCategoryMapper;

    @InjectMocks
    private ArtistMapperImpl artistMapper;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldMapArtistToResponseWithEventsCorrectly() {
        Event event = Event.builder()
                .id(1L)
                .name("EventName")
                .eventCategory(EventCategory.CONCERT)
                .build();

        Artist artist = Artist.builder()
                .firstName("John")
                .lastName("Doe")
                .nickname("J-D")
                .description("Great artist")
                .events(List.of(event))
                .build();

        when(eventCategoryMapper.toResponse(EventCategory.CONCERT)).thenReturn(EventCategoryDTO.CONCERT);

        ArtistResponse response = artistMapper.toResponse(artist);

        assertEquals("John", response.firstName());
        assertEquals("Doe", response.lastName());
        assertEquals("J-D", response.nickname());
        assertEquals("Great artist", response.description());
        assertEquals(1, response.events().size());
    }

    @Test
    void shouldMapRequestToArtistWithEventIdsCorrectly() {
        ArtistRequest request = new ArtistRequest("John", "Doe", "J-D", "Awesome", 5L,List.of(10L));

        Artist result = artistMapper.toEntity(request);

        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        assertEquals("J-D", result.getNickname());
        assertEquals("Awesome", result.getDescription());
        assertEquals(1, result.getEvents().size());
        assertEquals(10L, result.getEvents().get(0).getId());
        assertEquals(5L, result.getCreatorId());
    }
}
