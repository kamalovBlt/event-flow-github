package com.technokratos.repository.interfaces;

import com.technokratos.model.Event;

import java.util.List;
import java.util.Optional;

public interface EventRepository {
    long save(Event event);

    Optional<Event> findById(Long id);

    List<Event> findAll(int page, int size);

    void update(Event event);

    void deleteById(Long id);

    List<Event> findByArtistId(Long artistId);

    List<Event> findAllSimple(int page, int size);

    Optional<Event> findByIdSimple(Long id);
}
