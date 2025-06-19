package com.technokratos.repository.interfaces;

import com.technokratos.model.Artist;

import java.util.List;
import java.util.Optional;

public interface ArtistRepository {
    Optional<Long> save(Artist artist);

    List<Artist> findAll(String keywords, int page, int size);

    Optional<Artist> findById(Long id);

    List<Artist> findAll(int page, int size);

    void update(Artist artist);

    void deleteById(Long id);

    Optional<Long> findCreatorIdByArtistId(Long artistId);

    boolean existsById(Long id);

    boolean existsByNickname(String nickname);
}
