package com.technokratos.repository.impl;

import com.technokratos.exception.artist.ArtistNotFoundException;
import com.technokratos.model.Artist;
import com.technokratos.model.Event;
import com.technokratos.repository.interfaces.EventRepository;
import com.technokratos.repository.util.EventListResultSetExtractor;
import com.technokratos.repository.util.EventResultSetExtractor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
@Slf4j
public class PostgresEventRepository implements EventRepository {
    /*language=sql*/
    private static final String FIND_CREATOR_ID_BY_ID_QUERY = """
            SELECT e.creator_id
            FROM event e
            WHERE e.id = ? AND e.deleted = false
            """;

    /*language=sql*/
    private static final String EXIST_ARTISTS_COUNT_BY_ID_QUERY = """
            SELECT COUNT(*) FROM artist WHERE id IN (%s) AND deleted = false
            """;

    /*language=sql*/
    private static final String EXIST_EVENT_BY_ID_QUERY = """
            SELECT EXISTS (SELECT 1 FROM event WHERE id = ? AND deleted = false)
            """;

    /*language=sql*/
    private static final String EXIST_EVENT_BY_LOCATION_ID_AND_HALL_ID_AND_TIME_QUERY = """
            SELECT COUNT(*) > 0 FROM event
            WHERE location_id = ? AND hall_id = ? AND start_time < ? AND end_time > ?
            """;
    /*language=sql*/
    private static final String SAVE_QUERY = """
            INSERT INTO event (name, category, location_id, hall_id, description, start_time, end_time, canceled, video_key, popularity, creator_id)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            RETURNING id
            """;

    /*language=sql*/
    private static final String FIND_BY_ID_QUERY = """
            SELECT
                e.id, e.name, e.category AS event_category_name, e.location_id, e.hall_id,
                e.description, e.start_time, e.end_time , e.canceled, e.video_key, e.popularity,
                e.deleted, e.creator_id,
                ei.id AS image_id,
                a.id AS artist_id, a.first_name as artist_first_name,
                a.last_name as artist_last_name, a.nickname as artist_nickname
            FROM event e
            LEFT JOIN event_image ei ON e.id = ei.event_id
            LEFT JOIN event_artist ea ON e.id = ea.event_id
            LEFT JOIN artist a ON ea.artist_id = a.id and a.deleted = false
            WHERE e.id = ? AND e.deleted = false
            """;

    /*language=sql*/
    private static final String FIND_ALL_QUERY = """
            SELECT
                e.id, e.name, e.category AS event_category_name, e.location_id, e.hall_id,
                e.description, e.start_time, e.end_time, e.canceled, e.video_key, e.popularity,
                e.deleted, e.creator_id,
                ei.id AS image_id,
                a.id AS artist_id, a.first_name as artist_first_name,
                a.last_name as artist_last_name, a.nickname as artist_nickname
            FROM event e
            LEFT JOIN event_image ei ON e.id = ei.event_id
            LEFT JOIN event_artist ea ON e.id = ea.event_id
            LEFT JOIN artist a ON ea.artist_id = a.id and a.deleted = false
            WHERE e.deleted = false
            ORDER BY e.start_time DESC
            LIMIT ? OFFSET ?
            """;

    /*language=sql*/
    private static final String UPDATE_QUERY = """
            UPDATE event SET name = ?, category = ?, location_id = ?, hall_id = ?, description = ?,
            start_time = ?, end_time = ?, canceled = ?, video_key = ?, popularity = ?, creator_id = ?
            WHERE id = ?
            """;

    /*language=sql*/
    private static final String DELETE_BY_ID_QUERY = """
            UPDATE event SET deleted = true WHERE id = ?
            """;

    /*language=sql*/
    private static final String INSERT_EVENT_ARTIST_LINK = """
            INSERT INTO event_artist (event_id, artist_id) VALUES (?, ?)
            """;

    /*language=sql*/
    private static final String DELETE_EVENT_ARTIST_LINK = """
            DELETE FROM event_artist WHERE event_id = ?
            """;

    /*language=sql*/
    private static final String DELETE_EVENT_IMAGE = """
            DELETE FROM event_image WHERE event_id = ?
            """;

    private final JdbcTemplate jdbcTemplate;
    private final EventResultSetExtractor eventResultSetExtractor;
    private final EventListResultSetExtractor eventListResultSetExtractor;

    @Transactional
    @Override
    public Optional<Long> save(Event event) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(SAVE_QUERY, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, event.getName());
            ps.setString(2, event.getEventCategory().toString());
            ps.setString(3, event.getLocationId());
            ps.setString(4, event.getHallId());
            ps.setString(5, event.getDescription());
            ps.setTimestamp(6, Timestamp.valueOf(event.getStart()));
            ps.setTimestamp(7, Timestamp.valueOf(event.getEnd()));
            ps.setBoolean(8, event.getCanceled());
            ps.setString(9, event.getVideoKey());
            ps.setInt(10, event.getPopularity());
            ps.setLong(11, event.getCreatorId());
            return ps;
        }, keyHolder);

        Number key = keyHolder.getKey();
        if (key == null) {
            return Optional.empty();
        }
        Long id = key.longValue();
        Object[] artistIds = allArtistExistAndNotDeleted(event.getArtists());
        if (artistIds != null) {
            List<Object[]> batchParams = Arrays.stream(artistIds)
                    .map(artistId -> new Object[]{id, artistId})
                    .toList();
            jdbcTemplate.batchUpdate(INSERT_EVENT_ARTIST_LINK, batchParams);
        }

        return Optional.of(id);
    }

    @Override
    public Optional<Event> findById(Long id) {
        Event event = jdbcTemplate.query(FIND_BY_ID_QUERY, eventResultSetExtractor, id);
        return Optional.ofNullable(event);
    }

    @Override
    public List<Event> findAll(int page, int size) {
        int offset = page * size;
        return jdbcTemplate.query(FIND_ALL_QUERY, eventListResultSetExtractor, size, offset);
    }

    @Transactional
    @Override
    public void update(Event event) {
        jdbcTemplate.update(UPDATE_QUERY,
                event.getName(),
                event.getEventCategory().toString(),
                event.getLocationId(),
                event.getHallId(),
                event.getDescription(),
                Timestamp.valueOf(event.getStart()),
                Timestamp.valueOf(event.getEnd()),
                event.getCanceled(),
                event.getVideoKey(),
                event.getPopularity(),
                event.getCreatorId(),
                event.getId()
                );
        Object[] artistIds = allArtistExistAndNotDeleted(event.getArtists());
        if (artistIds != null) {
            List<Object[]> batchParams = Arrays.stream(artistIds)
                    .map(artistId -> new Object[]{event.getId(), artistId})
                    .toList();
            jdbcTemplate.update(DELETE_EVENT_ARTIST_LINK, event.getId());
            jdbcTemplate.batchUpdate(INSERT_EVENT_ARTIST_LINK, batchParams);
        }
        else if (event.getArtists() == null || event.getArtists().isEmpty()) {
            jdbcTemplate.update(DELETE_EVENT_ARTIST_LINK, event.getId());
        }
    }

    @Transactional
    @Override
    public void deleteById(Long id) {
        jdbcTemplate.update(DELETE_BY_ID_QUERY, id);
        jdbcTemplate.update(DELETE_EVENT_ARTIST_LINK, id);
        jdbcTemplate.update(DELETE_EVENT_IMAGE, id);
    }

    @Override
    public boolean existsById(Long id) {
        Boolean exists = jdbcTemplate.queryForObject(EXIST_EVENT_BY_ID_QUERY, Boolean.class, id);
        return Boolean.TRUE.equals(exists);
    }

    @Override
    public boolean existsByTimeAndLocationIdAndHallId(String locationId, String hallId,
                                                      LocalDateTime start, LocalDateTime end) {
        Boolean exists = jdbcTemplate.queryForObject(EXIST_EVENT_BY_LOCATION_ID_AND_HALL_ID_AND_TIME_QUERY,
                Boolean.class, locationId, hallId, end, start);
        return Boolean.TRUE.equals(exists);
    }

    @Override
    public Optional<Long> findCreatorIdByEventId(Long eventId) {
        try {
            Long creatorId = jdbcTemplate.queryForObject(FIND_CREATOR_ID_BY_ID_QUERY, Long.class, eventId);
            return Optional.ofNullable(creatorId);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    /**
     * Возвращает ID артистов как массив
     */
    private Object[] allArtistExistAndNotDeleted(List<Artist> artists) {
        if (artists == null || artists.isEmpty()) {
            return null;
        }
        Object[] artistIds = artists.stream().map(Artist::getId).toArray();
        String placeholders = Arrays.stream(artistIds)
                .map(id -> "?")
                .collect(Collectors.joining(", "));
        String sql = EXIST_ARTISTS_COUNT_BY_ID_QUERY.formatted(placeholders);
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, artistIds);
        if (count == null) {
            log.error("queryForObject вернул null для {}", artists);
            throw new RuntimeException("Внутренняя ошибка");
        }
        if (count != artists.size()) {
            throw new ArtistNotFoundException("""
                    К мероприятию добавлены несуществующие артисты. Удалите для корректного сохранения""");
        }
        return artistIds;
    }
}

