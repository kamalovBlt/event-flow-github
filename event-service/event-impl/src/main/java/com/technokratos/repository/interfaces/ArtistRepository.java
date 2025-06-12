package com.technokratos.repository.interfaces;

import com.technokratos.model.Artist;

import java.util.List;
import java.util.Optional;

public interface ArtistRepository {
    long save(Artist artist);

    Optional<Artist> findById(Long id);

    List<Artist> findAll(int page, int size);

    void update(Artist artist);

    void deleteById(Long id);
}
