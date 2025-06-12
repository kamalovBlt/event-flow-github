package com.technokratos.repository.impl;

import com.technokratos.model.Artist;
import com.technokratos.model.Event;
import com.technokratos.model.EventCategory;
import com.technokratos.repository.interfaces.EventRepository;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Statement;
import java.sql.Timestamp;
import java.util.*;

@Repository
public class EventRepositoryImpl implements EventRepository {
    private final JdbcTemplate jdbcTemplate;

    private static final String SAVE_QUERY = """
            INSERT INTO event (name, category_id, location_id, hall_id, description, date, canceled, video_key, popularity)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
            RETURNING id
            """;

    private static final String FIND_BY_ID_QUERY = """
            SELECT
                e.id AS event_id, e.name, e.category_id, e.location_id, e.hall_id, e.description,
                e.date, e.canceled, e.video_key, e.popularity, e.deleted,
                ec.id AS ec_id, ec.name AS ec_name, ec.deleted AS ec_deleted,
            
                ei.key AS image_key,
            
                a.id AS artist_id, a.first_name, a.last_name, a.nickname, a.description AS artist_description, a.deleted AS artist_deleted,
            
                eo.organizer_id
            
            FROM event e
            JOIN event_category ec ON e.category_id = ec.id
            LEFT JOIN event_image ei ON e.id = ei.event_id
            LEFT JOIN event_artist ea ON e.id = ea.event_id
            LEFT JOIN artist a ON ea.artist_id = a.id
            LEFT JOIN event_organizer eo ON e.id = eo.event_id
            WHERE e.id = ? AND e.deleted = false AND ec.deleted = false
            """;

    private static final String FIND_ALL_QUERY = """
            SELECT
                e.id AS event_id, e.name, e.category_id, e.location_id, e.hall_id, e.description,
                e.date, e.canceled, e.video_key, e.popularity, e.deleted,
            
                ec.id AS ec_id, ec.name AS ec_name, ec.deleted AS ec_deleted,
            
                ei.key AS image_key,
            
                a.id AS artist_id, a.first_name, a.last_name, a.nickname, a.description AS artist_description, a.deleted AS artist_deleted,
                eo.organizer_id
            FROM event e
            JOIN event_category ec ON e.category_id = ec.id
            LEFT JOIN event_image ei ON e.id = ei.event_id
            LEFT JOIN event_artist ea ON e.id = ea.event_id
            LEFT JOIN artist a ON ea.artist_id = a.id
            LEFT JOIN event_organizer eo ON e.id = eo.event_id
            WHERE e.deleted = false AND ec.deleted = false
            ORDER BY e.date DESC
            LIMIT ? OFFSET ?
            """;

    private static final String UPDATE_QUERY = """
            UPDATE event SET name = ?, category_id = ?, location_id = ?, hall_id = ?, description = ?,
            date = ?, canceled = ?, video_key = ?, popularity = ?
            WHERE id = ?
            """;

    private static final String DELETE_BY_ID_QUERY = """
            UPDATE event SET deleted = true WHERE id = ?
            """;

    private static final String FIND_EVENTS_BY_ARTIST_ID_QUERY = """
            SELECT
                e.id, e.name, e.category_id, e.location_id, e.hall_id, e.description,
                e.date, e.canceled, e.video_key, e.popularity, e.deleted,
                ec.id AS ec_id, ec.name AS ec_name, ec.deleted AS ec_deleted
            FROM event e
            JOIN event_category ec ON e.category_id = ec.id
            JOIN event_artist ea ON e.id = ea.event_id
            WHERE ea.artist_id = ? AND e.deleted = false AND ec.deleted = false
            """;

    private static final String FIND_ALL_SIMPLE_QUERY = """
            SELECT
                e.id AS event_id, e.name, e.category_id, e.location_id, e.hall_id, e.description,
                e.date, e.canceled, e.video_key, e.popularity, e.deleted,
            
                ec.id AS ec_id, ec.name AS ec_name, ec.deleted AS ec_deleted
            FROM event e
            JOIN event_category ec ON e.category_id = ec.id
            WHERE e.deleted = false AND ec.deleted = false
            ORDER BY e.date DESC
            LIMIT ? OFFSET ?
            """;

    private static final String FIND_BY_ID_SIMPLE_QUERY = """
            SELECT
                e.id AS event_id, e.name, e.category_id, e.location_id, e.hall_id, e.description,
                e.date, e.canceled, e.video_key, e.popularity, e.deleted,
            
                ec.id AS ec_id, ec.name AS ec_name, ec.deleted AS ec_deleted
            FROM event e
            JOIN event_category ec ON e.category_id = ec.id
            WHERE e.id = ? AND e.deleted = false AND ec.deleted = false
            """;

    public EventRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public long save(Event object) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            var ps = connection.prepareStatement(SAVE_QUERY, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, object.getName());
            ps.setLong(2, object.getEventCategory().getId());
            ps.setString(3, object.getLocationId());
            ps.setLong(4, object.getHallId());
            ps.setString(5, object.getDescription());
            ps.setTimestamp(6, Timestamp.valueOf(object.getDate()));
            ps.setBoolean(7, object.getCanceled());
            ps.setString(8, object.getVideoKey());
            ps.setInt(9, object.getPopularity());
            return ps;
        }, keyHolder);
        Number key = keyHolder.getKey();
        return key != null ? key.longValue() : -1;
    }

    @Override
    public Optional<Event> findById(Long id) {
        try {
            return jdbcTemplate.query(FIND_BY_ID_QUERY, eventWithDetailsExtractor, id);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public List<Event> findAll(int page, int size) {
        int offset = (page - 1) * size;
        return jdbcTemplate.query(FIND_ALL_QUERY, eventsWithDetailsExtractor, size, offset);
    }


    @Override
    public void update(Event object) {
        jdbcTemplate.update(UPDATE_QUERY,
                object.getName(), object.getEventCategory().getId(), object.getLocationId(), object.getHallId(),
                object.getDescription(), Timestamp.valueOf(object.getDate()), object.getCanceled(),
                object.getVideoKey(), object.getPopularity(), object.getId());
    }

    @Override
    public void deleteById(Long id) {
        jdbcTemplate.update(DELETE_BY_ID_QUERY, id);
    }

    @Override
    public List<Event> findByArtistId(Long artistId) {
        return jdbcTemplate.query(FIND_EVENTS_BY_ARTIST_ID_QUERY, mapper, artistId);
    }

    @Override
    public List<Event> findAllSimple(int page, int size) {
        int offset = (page - 1)  * size;
        return jdbcTemplate.query(FIND_ALL_SIMPLE_QUERY, mapper, size, offset);
    }

    @Override
    public Optional<Event> findByIdSimple(Long id) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(FIND_BY_ID_SIMPLE_QUERY, mapper, id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    private final RowMapper<Event> mapper = (rs, rowNum) -> Event.builder()
            .id(rs.getLong("id"))
            .name(rs.getString("name"))
            .eventCategory(EventCategory.builder()
                    .id(rs.getLong("ec_id"))
                    .name(rs.getString("ec_name"))
                    .deleted(rs.getBoolean("ec_deleted"))
                    .build())
            .locationId(rs.getString("location_id"))
            .hallId(rs.getLong("hall_id"))
            .description(rs.getString("description"))
            .date(rs.getTimestamp("date").toLocalDateTime())
            .canceled(rs.getBoolean("canceled"))
            .videoKey(rs.getString("video_key"))
            .popularity(rs.getInt("popularity"))
            .deleted(rs.getBoolean("deleted"))
            .build();

    private final ResultSetExtractor<Optional<Event>> eventWithDetailsExtractor = rs -> {
        Map<Long, Event> eventMap = new HashMap<>();

        while (rs.next()) {
            Long eventId = rs.getLong("event_id");
            Event event = eventMap.get(eventId);
            if (event == null) {
                event = Event.builder()
                        .id(eventId)
                        .name(rs.getString("name"))
                        .eventCategory(EventCategory.builder()
                                .id(rs.getLong("ec_id"))
                                .name(rs.getString("ec_name"))
                                .deleted(rs.getBoolean("ec_deleted"))
                                .build())
                        .locationId(rs.getString("location_id"))
                        .hallId(rs.getLong("hall_id"))
                        .description(rs.getString("description"))
                        .date(rs.getTimestamp("date").toLocalDateTime())
                        .canceled(rs.getBoolean("canceled"))
                        .videoKey(rs.getString("video_key"))
                        .popularity(rs.getInt("popularity"))
                        .deleted(rs.getBoolean("deleted"))
                        .imageKeys(new HashSet<>())
                        .artists(new ArrayList<>())
                        .organizerIds(new ArrayList<>())
                        .build();
                eventMap.put(eventId, event);
            }

            String imageKey = rs.getString("image_key");
            if (imageKey != null) {
                event.getImageKeys().add(imageKey);
            }

            Long artistId = rs.getLong("artist_id");
            if (artistId != 0 && rs.getObject("artist_id") != null) {
                Artist artist = Artist.builder()
                        .id(artistId)
                        .firstName(rs.getString("first_name"))
                        .lastName(rs.getString("last_name"))
                        .nickname(rs.getString("nickname"))
                        .description(rs.getString("artist_description"))
                        .deleted(rs.getBoolean("artist_deleted"))
                        .build();
                if (!event.getArtists().contains(artist)) {
                    event.getArtists().add(artist);
                }
            }

            Long organizerId = rs.getLong("organizer_id");
            if (!event.getOrganizerIds().contains(organizerId)) {
                event.getOrganizerIds().add(organizerId);
            }
        }

        return eventMap.values().stream().findFirst();
    };

    private final ResultSetExtractor<List<Event>> eventsWithDetailsExtractor = rs -> {
        Map<Long, Event> eventMap = new LinkedHashMap<>();

        while (rs.next()) {
            Long eventId = rs.getLong("event_id");
            Event event = eventMap.get(eventId);
            if (event == null) {
                event = Event.builder()
                        .id(eventId)
                        .name(rs.getString("name"))
                        .eventCategory(EventCategory.builder()
                                .id(rs.getLong("ec_id"))
                                .name(rs.getString("ec_name"))
                                .deleted(rs.getBoolean("ec_deleted"))
                                .build())
                        .locationId(rs.getString("location_id"))
                        .hallId(rs.getLong("hall_id"))
                        .description(rs.getString("description"))
                        .date(rs.getTimestamp("date").toLocalDateTime())
                        .canceled(rs.getBoolean("canceled"))
                        .videoKey(rs.getString("video_key"))
                        .popularity(rs.getInt("popularity"))
                        .deleted(rs.getBoolean("deleted"))
                        .imageKeys(new HashSet<>())
                        .artists(new ArrayList<>())
                        .organizerIds(new ArrayList<>())
                        .build();
                eventMap.put(eventId, event);
            }

            String imageKey = rs.getString("image_key");
            if (imageKey != null) {
                event.getImageKeys().add(imageKey);
            }

            Long artistId = rs.getLong("artist_id");
            if (artistId != 0 && rs.getObject("artist_id") != null) {
                Artist artist = Artist.builder()
                        .id(artistId)
                        .firstName(rs.getString("first_name"))
                        .lastName(rs.getString("last_name"))
                        .nickname(rs.getString("nickname"))
                        .description(rs.getString("artist_description"))
                        .deleted(rs.getBoolean("artist_deleted"))
                        .build();
                if (!event.getArtists().contains(artist)) {
                    event.getArtists().add(artist);
                }
            }

            Long organizerId = rs.getLong("organizer_id");
            if (!event.getOrganizerIds().contains(organizerId)) {
                event.getOrganizerIds().add(organizerId);
            }
        }

        return new ArrayList<>(eventMap.values());
    };

}
