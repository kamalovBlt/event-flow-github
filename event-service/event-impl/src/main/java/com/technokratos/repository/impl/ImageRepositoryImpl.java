package com.technokratos.repository.impl;

import com.technokratos.model.Image;
import com.technokratos.repository.interfaces.ImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ImageRepositoryImpl implements ImageRepository {

    private final JdbcTemplate jdbcTemplate;

    private static final String SAVE_QUERY = """
            INSERT INTO event_image (event_id, key)
            VALUES (?, ?)
            RETURNING id
            """;

    private static final String FIND_BY_ID_QUERY = """
            SELECT id, event_id, key
            FROM event_image
            WHERE id = ?
            """;

    private static final String DELETE_BY_ID_QUERY = """
            DELETE FROM event_image
            WHERE id = ?
            """;

    private static final String UPDATE_QUERY = """
            UPDATE event_image
            SET event_id = ?, key = ?
            WHERE id = ?
            """;

    @Override
    public Optional<Long> save(Image image) {
        return jdbcTemplate.query(SAVE_QUERY, rs -> {
            if (rs.next()) {
                return Optional.of(rs.getLong("id"));
            }
            return Optional.empty();
        }, image.getEventId(), image.getKey());
    }

    @Override
    public Optional<Image> findById(Long id) {
        return jdbcTemplate.query(FIND_BY_ID_QUERY, rs -> {
            if (rs.next()) {
                return Optional.of(Image.builder()
                        .id(rs.getLong("id"))
                        .eventId(rs.getLong("event_id"))
                        .key(rs.getString("key"))
                        .build());
            }
            return Optional.empty();
        }, id);
    }

    @Override
    public void deleteById(Long id) {
        jdbcTemplate.update(DELETE_BY_ID_QUERY, id);
    }

    @Override
    public void update(Image image) {
        jdbcTemplate.update(UPDATE_QUERY, image.getEventId(), image.getKey(), image.getId());
    }
}
