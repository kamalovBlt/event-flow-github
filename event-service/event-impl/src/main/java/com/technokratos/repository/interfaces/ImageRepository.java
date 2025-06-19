package com.technokratos.repository.interfaces;

import com.technokratos.model.Image;

import java.util.Optional;

public interface ImageRepository {
    Optional<Long> save(Image image);
    Optional<Image> findById(Long id);
    void deleteById(Long id);
    void update(Image image);
}
