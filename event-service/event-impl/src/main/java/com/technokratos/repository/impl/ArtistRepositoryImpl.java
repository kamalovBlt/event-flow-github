package com.technokratos.repository.impl;

import com.technokratos.model.Artist;
import com.technokratos.repository.interfaces.ArtistRepository;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
public class ArtistRepositoryImpl implements ArtistRepository {
    private final JdbcTemplate jdbcTemplate;

    private final static String SAVE_QUERY = """
                INSERT INTO artist (first_name, last_name, nickname, description)
                VALUES (?, ?, ?, ?)
                RETURNING id
            """;

    private final static String FIND_BY_ID_QUERY = """
            SELECT id, first_name, last_name, nickname, description, deleted
            FROM artist
            WHERE id = ? AND deleted = false
            """;
    private final static String FIND_ALL_QUERY = """
                SELECT id, first_name, last_name, nickname, description, deleted FROM artist
                WHERE deleted = false
                ORDER BY id
                LIMIT ? OFFSET ?
            """;
    private final static String UPDATE_QUERY = """
                UPDATE artist SET first_name = ?, last_name = ?, nickname = ?, description = ?
                WHERE id = ?
            """;
    private final static String DELETE_BY_ID_QUERY = """
                UPDATE artist SET deleted = true WHERE id = ?
            """;

    public ArtistRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public long save(Artist artist) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            var ps = connection.prepareStatement(SAVE_QUERY, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, artist.getFirstName());
            ps.setString(2, artist.getLastName());
            ps.setString(3, artist.getNickname());
            ps.setString(4, artist.getDescription());
            return ps;
        }, keyHolder);

        Number key = keyHolder.getKey();

        return key != null ? key.longValue() : -1;
    }

    @Override
    public Optional<Artist> findById(Long id) {
        try {
            Artist artist = jdbcTemplate.queryForObject(FIND_BY_ID_QUERY, artistMapper, id);
            return Optional.ofNullable(artist);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Artist> findAll(int page, int size) {
        int offset = (page - 1) * size;
        return jdbcTemplate.query(FIND_ALL_QUERY, artistMapper, size, offset);
    }


    @Override
    public void update(Artist artist) {
        jdbcTemplate.update(UPDATE_QUERY, artist.getFirstName(), artist.getLastName(), artist.getNickname(),
                artist.getDescription(), artist.getId());
    }

    @Override
    public void deleteById(Long id) {
        jdbcTemplate.update(DELETE_BY_ID_QUERY, id);
    }

    private final RowMapper<Artist> artistMapper = (rs, rowNum) -> {
        Long id = rs.getLong("id");
        return Artist.builder()
                .id(id)
                .firstName(rs.getString("first_name"))
                .lastName(rs.getString("last_name"))
                .nickname(rs.getString("nickname"))
                .description(rs.getString("description"))
                .deleted(rs.getBoolean("deleted"))
                .events(null)
                .build();
    };
}
