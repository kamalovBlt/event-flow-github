package com.technokratos;

import com.technokratos.model.EventCategory;
import com.technokratos.repository.impl.EventCategoryRepositoryImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class EventCategoryRepositoryImplTest extends RepositoryTestBase {

    @Autowired
    private EventCategoryRepositoryImpl eventCategoryRepository;

    @Test
    void shouldSaveAndFindById() {
        EventCategory category = new EventCategory(1L, "Music", false);
        long id = eventCategoryRepository.save(category);

        assertTrue(id > 0);

        Optional<EventCategory> found = eventCategoryRepository.findById(id);
        assertTrue(found.isPresent());
        assertEquals("Music", found.get().getName());
    }

    @Test
    void shouldFindByIdNotFound() {
        Optional<EventCategory> found = eventCategoryRepository.findById(999L);
        assertFalse(found.isPresent());
    }

    @Test
    void shouldFindAll() {
        eventCategoryRepository.save(new EventCategory(1L, "Music", false));
        eventCategoryRepository.save(new EventCategory(2L, "Theater", false));

        List<EventCategory> categories = eventCategoryRepository.findAll(1, 10);
        assertEquals(2, categories.size());
    }

    @Test
    void shouldUpdate() {
        EventCategory category = new EventCategory(1L, "Music", false);

        long categoryId = eventCategoryRepository.save(category);

        category.setId(categoryId);

        category.setName("Updated Music");
        eventCategoryRepository.update(category);

        Optional<EventCategory> updated = eventCategoryRepository.findById(categoryId);
        assertTrue(updated.isPresent());
        assertEquals("Updated Music", updated.get().getName());
    }

    @Test
    void shouldDelete() {
        EventCategory category = new EventCategory(1L, "Music", false);
        long categoryId = eventCategoryRepository.save(category);

        eventCategoryRepository.deleteById(categoryId);

        Optional<EventCategory> deleted = eventCategoryRepository.findById(categoryId);
        assertFalse(deleted.isPresent());
    }
}