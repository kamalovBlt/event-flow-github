package com.technokratos.repository.interfaces;

import com.technokratos.model.Event;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EventRepository {
    Optional<Long> save(Event event);

    Optional<Event> findById(Long id);

    List<Event> findAll(int page, int size);

    void update(Event event);

    void deleteById(Long id);

    boolean existsById(Long id);

    boolean existsByTimeAndLocationIdAndHallId(String locationId, String hallId, LocalDateTime start, LocalDateTime end);

    Optional<Long> findCreatorIdByEventId(Long eventId);
}
