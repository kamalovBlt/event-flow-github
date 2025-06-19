package com.technokratos.repository.impl;

import com.technokratos.exception.event.EventNotFoundException;
import com.technokratos.model.Artist;
import com.technokratos.model.Event;
import com.technokratos.repository.interfaces.ArtistRepository;
import com.technokratos.repository.util.ArtistListResultSetExtractor;
import com.technokratos.repository.util.ArtistResultSetExtractor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
@Slf4j
public class PostgresArtistRepository implements ArtistRepository {

    /*language=sql*/
    private static final String FIND_CREATOR_ID_BY_ID_QUERY = """
            SELECT a.creator_id
            FROM artist a
            WHERE a.id = ? AND a.deleted = false
            """;
    /*language=sql*/
    private static final String EXIST_ARTIST_BY_ID_QUERY = """
            SELECT EXISTS (SELECT 1 FROM artist WHERE id = ? AND deleted = false)
            """;

    /*language=sql*/
    private static final String EXIST_ARTIST_BY_NICKNAME_QUERY = """
            SELECT EXISTS (SELECT 1 FROM artist WHERE nickname = ? AND deleted = false)
            """;

    /*language=sql*/
    private static final String EXIST_EVENTS_COUNT_BY_ID_QUERY = """
            SELECT COUNT(*) FROM event WHERE id IN (%s) AND deleted = false
            """;
    /*language=sql*/
    private static final String SAVE_QUERY = """
            INSERT INTO artist (first_name, last_name, nickname, description,creator_id)
            VALUES (?, ?, ?, ?,?)
            RETURNING id
            """;

    /*language=sql*/
    private static final String FIND_ALL_BY_KEYWORDS_QUERY = """
            SELECT a.id, a.first_name, a.last_name, a.nickname,
                   a.description, a.creator_id,
                   e.id AS event_id, e.name AS event_name,
                   e.category AS event_category_name
            FROM artist a
            LEFT JOIN event_artist ea ON a.id = ea.artist_id
            LEFT JOIN event e ON ea.event_id = e.id AND e.deleted = false
            WHERE a.deleted = false AND a.search_vector @@ plainto_tsquery('russian', ?)
            ORDER BY a.id
            LIMIT ? OFFSET ?
            """;

    /*language=sql*/
    private static final String FIND_BY_ID_NOT_DELETED_WITH_EVENTS_QUERY = """
            SELECT a.id, a.first_name, a.last_name, a.nickname,
                   a.description, a.creator_id,
                   e.id AS event_id, e.name AS event_name,
                   e.category AS event_category_name
            FROM artist a
            LEFT JOIN event_artist ea ON a.id = ea.artist_id
            LEFT JOIN event e ON ea.event_id = e.id AND e.deleted = false
            WHERE a.id = ? AND a.deleted = false
            """;

    /*language=sql*/
    private static final String FIND_ALL_NOT_DELETED_WITH_EVENTS_QUERY = """
            SELECT a.id, a.first_name, a.last_name, a.nickname,
                   a.description, a.creator_id,
                   e.id AS event_id, e.name AS event_name,
                   e.category AS event_category_name
            FROM artist a
            LEFT JOIN event_artist ea ON a.id = ea.artist_id
            LEFT JOIN event e ON ea.event_id = e.id AND e.deleted = false
            WHERE a.deleted = false
            ORDER BY a.id
            LIMIT ? OFFSET ?
            """;

    /*language=sql*/
    private static final String UPDATE_QUERY = """
            UPDATE artist SET first_name = ?, last_name = ?, nickname = ?, description = ?, creator_id = ?
            WHERE id = ? AND deleted = false
            """;
    /*language=sql*/
    private static final String INSERT_EVENT_ARTIST_LINK = """
            INSERT INTO event_artist (event_id, artist_id) VALUES (?, ?)
            """;
    private static final String DELETE_BY_ID_QUERY = """
            UPDATE artist SET deleted = true WHERE id = ?
            """;
    /*language=sql*/
    private static final String DELETE_EVENT_ARTIST_LINK = """
            DELETE FROM event_artist WHERE artist_id = ?
            """;

    private final JdbcTemplate jdbcTemplate;
    private final ArtistResultSetExtractor artistResultSetExtractor;
    private final ArtistListResultSetExtractor artistListResultSetExtractor;

    @Transactional
    @Override
    public Optional<Long> save(Artist artist) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(SAVE_QUERY, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, artist.getFirstName());
            ps.setString(2, artist.getLastName());
            ps.setString(3, artist.getNickname());
            ps.setString(4, artist.getDescription());
            ps.setLong(5, artist.getCreatorId());
            return ps;
        }, keyHolder);
        Number key = keyHolder.getKey();
        if (key == null) {
            return Optional.empty();
        }
        Long id = key.longValue();
        Object[] eventIds = allEventsExistAndNotDeleted(artist.getEvents());
        if (eventIds != null) {
            List<Object[]> batchParams = Arrays.stream(eventIds)
                    .map(eventId -> new Object[]{eventId, id})
                    .toList();
            jdbcTemplate.batchUpdate(INSERT_EVENT_ARTIST_LINK, batchParams);
        }
        return Optional.of(id);
    }

    @Override
    public List<Artist> findAll(String keywords, int page, int size) {
        int offset = page * size;
        return jdbcTemplate.query(FIND_ALL_BY_KEYWORDS_QUERY, artistListResultSetExtractor, keywords, size, offset);
    }

    @Override
    public Optional<Artist> findById(Long id) {
        Artist artist = jdbcTemplate.query(FIND_BY_ID_NOT_DELETED_WITH_EVENTS_QUERY, artistResultSetExtractor, id);
        return Optional.ofNullable(artist);
    }

    @Override
    public List<Artist> findAll(int page, int size) {
        int offset = page * size;
        return jdbcTemplate.query(FIND_ALL_NOT_DELETED_WITH_EVENTS_QUERY, artistListResultSetExtractor, size, offset);
    }

    @Transactional
    @Override
    public void update(Artist artist) {
        jdbcTemplate.update(UPDATE_QUERY,
                artist.getFirstName(),
                artist.getLastName(),
                artist.getNickname(),
                artist.getDescription(),
                artist.getCreatorId(),
                artist.getId()
        );
        Object[] eventIds = allEventsExistAndNotDeleted(artist.getEvents());
        if (eventIds != null) {
            List<Object[]> batchParams = Arrays.stream(eventIds)
                    .map(eventId -> new Object[]{eventId, artist.getId()})
                    .toList();
            jdbcTemplate.update(DELETE_EVENT_ARTIST_LINK, artist.getId());
            jdbcTemplate.batchUpdate(INSERT_EVENT_ARTIST_LINK, batchParams);
        }
        else if (artist.getEvents() == null || artist.getEvents().isEmpty()) {
            jdbcTemplate.update(DELETE_EVENT_ARTIST_LINK, artist.getId());
        }
    }

    @Transactional
    @Override
    public void deleteById(Long id) {
        jdbcTemplate.update(DELETE_BY_ID_QUERY, id);
        jdbcTemplate.update(DELETE_EVENT_ARTIST_LINK, id);
    }

    @Override
    public Optional<Long> findCreatorIdByArtistId(Long artistId) {
        try {
            Long creatorId = jdbcTemplate.queryForObject(FIND_CREATOR_ID_BY_ID_QUERY, Long.class, artistId);
            return Optional.ofNullable(creatorId);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public boolean existsById(Long id) {
        Boolean exists = jdbcTemplate.queryForObject(EXIST_ARTIST_BY_ID_QUERY, Boolean.class, id);
        return Boolean.TRUE.equals(exists);
    }

    @Override
    public boolean existsByNickname(String nickname) {
        Boolean exists = jdbcTemplate.queryForObject(EXIST_ARTIST_BY_NICKNAME_QUERY, Boolean.class, nickname);
        return Boolean.TRUE.equals(exists);
    }

    /**
     * Возвращает ID мероприятий как массив
     */
    private Object[] allEventsExistAndNotDeleted(List<Event> events) {
        if (events == null || events.isEmpty()) {
            return null;
        }
        Object[] eventIds = events.stream().map(Event::getId).toArray();
        String placeholders = Arrays.stream(eventIds)
                .map(id -> "?")
                .collect(Collectors.joining(", "));
        String sql = String.format(EXIST_EVENTS_COUNT_BY_ID_QUERY, placeholders);
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, eventIds);
        if (count == null) {
            log.error("queryForObject вернул null для {}", events);
            throw new RuntimeException("Внутрення ошибка");
        }
        if (count != events.size()) {
            throw new EventNotFoundException("""
                    К артисту добавлены несуществующие мероприятия. Удалите для корректного сохранения""");
        }
        return eventIds;
    }


}
