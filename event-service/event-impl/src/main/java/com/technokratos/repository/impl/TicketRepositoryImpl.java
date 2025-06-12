package com.technokratos.repository.impl;

import com.technokratos.model.Ticket;
import com.technokratos.model.TicketId;
import com.technokratos.repository.interfaces.TicketRepository;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class TicketRepositoryImpl implements TicketRepository {
    private final JdbcTemplate jdbcTemplate;

    private final static String SAVE_QUERY = """
            INSERT INTO ticket (user_id, event_id, location_id, hall_id, row_id, seat_id, category_id, cost)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            RETURNING user_id, event_id, location_id, hall_id, row_id, seat_id
            """;

    private final static String FIND_BY_ID_QUERY = """
            SELECT user_id, event_id, location_id, hall_id, row_id, seat_id,
                   category_id, cost, deleted
            FROM ticket
            WHERE user_id = ? AND event_id = ? AND location_id = ?
              AND hall_id = ? AND row_id = ? AND seat_id = ?
              AND deleted = false
            """;

    private final static String FIND_ALL_QUERY = """
            SELECT user_id, event_id, location_id, hall_id, row_id, seat_id, category_id, cost, deleted
            FROM ticket
            WHERE deleted = false
            ORDER BY user_id
            LIMIT ? OFFSET ?
            """;

    private final static String UPDATE_QUERY = """
            UPDATE ticket SET event_id = ?, location_id = ?, hall_id = ?, row_id = ?, seat_id = ?, category_id = ?, cost = ?
            WHERE user_id = ? AND event_id = ?
            """;

    private final static String DELETE_BY_ID_QUERY = """
            UPDATE ticket SET deleted = true
            WHERE user_id = ? AND event_id = ? AND location_id = ? AND hall_id = ? AND row_id = ? AND seat_id = ?
            """;

    public TicketRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public TicketId save(Ticket object) {
        return jdbcTemplate.queryForObject(SAVE_QUERY,
                new Object[]{
                        object.getUserId(), object.getEventId(), object.getLocationId(),
                        object.getHallId(), object.getRowId(), object.getSeatId(),
                        object.getCategoryId(), object.getCost()
                },
                (rs, rowNum) -> new TicketId(
                        rs.getLong("user_id"),
                        rs.getLong("event_id"),
                        rs.getString("location_id"),
                        rs.getLong("hall_id"),
                        rs.getLong("row_id"),
                        rs.getLong("seat_id")
                )
        );
    }


    @Override
    public Optional<Ticket> findById(TicketId id) {
        try {
            Ticket ticket = jdbcTemplate.queryForObject(
                    FIND_BY_ID_QUERY,
                    mapper,
                    id.userId(), id.eventId(), id.locationId(),
                    id.hallId(), id.rowId(), id.seatId()
            );
            return Optional.ofNullable(ticket);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Ticket> findAll(int page, int size) {
        int offset = (page - 1) * size;
        return jdbcTemplate.query(FIND_ALL_QUERY, mapper, size, offset);
    }

    @Override
    public void update(Ticket object) {
        jdbcTemplate.update(UPDATE_QUERY,
                object.getEventId(), object.getLocationId(), object.getHallId(),
                object.getRowId(), object.getSeatId(), object.getCategoryId(),
                object.getCost(), object.getUserId(), object.getEventId());
    }

    @Override
    public void deleteById(TicketId id) {
        jdbcTemplate.update(DELETE_BY_ID_QUERY, id.userId(), id.eventId(), id.locationId(), id.hallId(), id.rowId(), id.seatId());
    }

    private final RowMapper<Ticket> mapper = (rs, rowNum) -> {
        Ticket ticket = Ticket.builder()
                .userId(rs.getLong("user_id"))
                .eventId(rs.getLong("event_id"))
                .locationId(rs.getString("location_id"))
                .hallId(rs.getLong("hall_id"))
                .rowId(rs.getLong("row_id"))
                .seatId(rs.getLong("seat_id"))
                .categoryId(rs.getInt("category_id"))
                .cost(rs.getBigDecimal("cost"))
                .deleted(rs.getBoolean("deleted"))
                .build();
        ticket.postLoad();
        return ticket;
    };
}
