package com.technokratos.repository.interfaces;

import com.technokratos.model.EventCategory;

import java.util.List;
import java.util.Optional;

public interface EventCategoryRepository {
    long save(EventCategory eventCategory);

    Optional<EventCategory> findById(Long id);

    List<EventCategory> findAll(int page, int size);

    void update(EventCategory eventCategory);

    void deleteById(Long id);
}
