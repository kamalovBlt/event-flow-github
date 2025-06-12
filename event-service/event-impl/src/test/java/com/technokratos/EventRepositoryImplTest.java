package com.technokratos;

import com.technokratos.model.Event;
import com.technokratos.model.EventCategory;
import com.technokratos.repository.impl.EventCategoryRepositoryImpl;
import com.technokratos.repository.impl.EventRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class EventRepositoryImplTest extends RepositoryTestBase {

    @Autowired
    private EventRepositoryImpl eventRepository;

    @Autowired
    private EventCategoryRepositoryImpl eventCategoryRepository;

    EventCategory category;

    @BeforeEach
    void setUp() {
        category = new EventCategory(1L, "Music", false);
        long categoryId = eventCategoryRepository.save(category);

        category.setId(categoryId);
    }

    @Test
    void shouldSaveAndFind() {
        Event event = Event.builder()
                .name("TestEvent")
                .eventCategory(category)
                .locationId("123")
                .hallId(2L)
                .description("Test description")
                .date(LocalDateTime.now())
                .canceled(false)
                .videoKey("testKey")
                .popularity(100)
                .build();

        long eventId = eventRepository.save(event);

        assertTrue(eventId > 0);

        Event foundEvent = eventRepository.findById(eventId)
                .orElseThrow(() -> new AssertionError("Event not found after save"));

        assertEquals("TestEvent", foundEvent.getName());
    }

    @Test
    void shouldFindByIdNotFound() {
        Optional<Event> result = eventRepository.findById(123L);

        assertFalse(result.isPresent());
    }

    @Test
    void shouldFindAll() {

        for (int i = 0; i < 5; i++) {
            eventRepository.save(Event.builder()
                    .name("Test")
                    .eventCategory(category)
                    .locationId("123")
                    .hallId(2L)
                    .description("Test description")
                    .date(LocalDateTime.now())
                    .canceled(false)
                    .videoKey("testKey")
                    .popularity(100)
                    .build()
            );
        }

        List<Event> result = eventRepository.findAll(1, 10);

        assertEquals(5, result.size());
        assertEquals("Test", result.getFirst().getName());
    }

    @Test
    void shouldUpdate() {
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

        Event event = Event.builder()
                .id(eventId)
                .name("UpdatedName")
                .eventCategory(category)
                .locationId("123")
                .hallId(2L)
                .description("Updated description")
                .date(LocalDateTime.now())
                .canceled(true)
                .videoKey("updatedKey")
                .popularity(999)
                .build();

        eventRepository.update(event);

        Event updatedEvent = eventRepository.findById(eventId)
                .orElseThrow(() -> new AssertionError("Event not found after update"));

        assertEquals("UpdatedName", updatedEvent.getName());
        assertEquals("updatedKey", updatedEvent.getVideoKey());
        assertEquals("Updated description", updatedEvent.getDescription());
    }

    @Test
    void shouldDeleteById() {
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

        eventRepository.deleteById(eventId);

        Optional<Event> deletedEvent = eventRepository.findById(eventId);
        assertFalse(deletedEvent.isPresent());
    }

}
