package com.technokratos;

import com.technokratos.model.Event;
import com.technokratos.model.EventCategory;
import com.technokratos.repository.impl.EventCategoryRepositoryImpl;
import com.technokratos.repository.impl.EventRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

public class EventRepositoryImplWithJdbcMockTest extends RepositoryTestBase{

    @Autowired
    private EventRepositoryImpl eventRepository;

    @Autowired
    private EventCategoryRepositoryImpl eventCategoryRepository;

    @MockitoBean
    JdbcTemplate jdbcTemplate;

    EventCategory category;

    @BeforeEach
    void setUp() {
        category = new EventCategory(1L, "Music", false);
        long categoryId = eventCategoryRepository.save(category);

        category.setId(categoryId);
    }

    @Test
    void shouldFindByArtistId() {
        long eventId = eventRepository.save(Event.builder()
                .name("Test")
                .eventCategory(category)
                .locationId("123")
                .hallId(2L)
                .description("Test description")
                .date(LocalDateTime.now())
                .canceled(false)
                .videoKey("testKey")
                .popularity(100)
                .build());

        when(jdbcTemplate.query(anyString(), any(RowMapper.class), eq(1L)))
                .thenReturn(List.of(Event.builder().id(eventId).name("ArtistEvent").build()));

        List<Event> result = eventRepository.findByArtistId(1L);

        assertEquals(1, result.size());
        assertEquals("ArtistEvent", result.getFirst().getName());
    }

    @Test
    void shouldFindAllSimple() {
        long eventId = eventRepository.save(Event.builder()
                .name("Test")
                .eventCategory(category)
                .locationId("123")
                .hallId(2L)
                .description("Test description")
                .date(LocalDateTime.now())
                .canceled(false)
                .videoKey("testKey")
                .popularity(100)
                .build());

        when(jdbcTemplate.query(anyString(), any(RowMapper.class), eq(10), eq(0)))
                .thenReturn(List.of(Event.builder().id(eventId).name("Simple").build()));

        List<Event> result = eventRepository.findAllSimple(1, 10);

        assertEquals(1, result.size());
        assertEquals("Simple", result.getFirst().getName());
    }

    @Test
    void shouldFindByIdSimpleFound() {
        long eventId = eventRepository.save(Event.builder()
                .name("Test")
                .eventCategory(category)
                .locationId("123")
                .hallId(2L)
                .description("Test description")
                .date(LocalDateTime.now())
                .canceled(false)
                .videoKey("testKey")
                .popularity(100)
                .build());

        when(jdbcTemplate.queryForObject(anyString(), any(RowMapper.class), eq(eventId)))
                .thenReturn(Event.builder().id(eventId).name("SimpleEvent").build());

        Optional<Event> result = eventRepository.findByIdSimple(eventId);

        assertTrue(result.isPresent());
        assertEquals("SimpleEvent", result.get().getName());
    }

    @Test
    void shouldFindByIdSimpleNotFound() {
        long eventId = eventRepository.save(Event.builder()
                .name("Test")
                .eventCategory(category)
                .locationId("123")
                .hallId(2L)
                .description("Test description")
                .date(LocalDateTime.now())
                .canceled(false)
                .videoKey("testKey")
                .popularity(100)
                .build());

        when(jdbcTemplate.queryForObject(anyString(), any(RowMapper.class), eq(eventId)))
                .thenThrow(new EmptyResultDataAccessException(1));

        Optional<Event> result = eventRepository.findByIdSimple(eventId);

        assertFalse(result.isPresent());
    }
}
