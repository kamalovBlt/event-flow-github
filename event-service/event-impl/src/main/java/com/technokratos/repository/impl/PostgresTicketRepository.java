package com.technokratos.repository.impl;

import com.technokratos.model.Ticket;
import com.technokratos.model.TicketCategory;
import com.technokratos.repository.interfaces.TicketRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.List;
import java.util.Optional;


@Repository
@RequiredArgsConstructor
@Slf4j
public class PostgresTicketRepository implements TicketRepository {

    /*language=sql*/
    private static final String SAVE_QUERY = """
            INSERT INTO ticket (event_id, location_id, hall_id, row_num, seat_num, category, cost)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            RETURNING id
            """;

    /*language=sql*/
    private static final String EXIST_TICKET_BY_ID_QUERY = """
            SELECT EXISTS (SELECT 1 FROM ticket WHERE id = ? AND event_id = ? AND deleted = false)
            """;

    /*language=sql*/
    private static final String FIND_BY_ID_QUERY = """
            SELECT
                t.id, t.user_id, t.event_id, t.location_id, t.hall_id,
                t.row_num, t.seat_num, t.category, t.cost, t.is_sell, t.deleted
            FROM ticket t
            WHERE t.id = ? AND t.event_id = ? AND t.deleted = false
            """;

    /*language=sql*/
    private static final String FIND_ALL_QUERY = """
            SELECT
                t.id, t.user_id, t.event_id, t.location_id, t.hall_id,
                t.row_num, t.seat_num, t.category, t.cost, t.is_sell, t.deleted
            FROM ticket t
            WHERE t.event_id = ? AND t.deleted = false
            ORDER BY t.id
            """;

    /*language=sql*/
    private static final String UPDATE_QUERY = """
            UPDATE ticket SET user_id = ?, location_id = ?, hall_id = ?,
                row_num = ?, seat_num = ?, category = ?, cost = ?, is_sell = ?
            WHERE id = ? AND event_id = ? AND deleted = false
            """;

    /*language=sql*/
    private static final String DELETE_BY_ID_QUERY = """
            UPDATE ticket SET deleted = true WHERE id = ? AND event_id = ?
            """;

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Ticket> ticketRowMapper = new RowMapper<>() {
        @Override
        public Ticket mapRow(ResultSet rs, int rowNum) throws SQLException {
            return Ticket.builder()
                    .id(rs.getLong("id"))
                    .userId(rs.getLong("user_id"))
                    .eventId(rs.getLong("event_id"))
                    .locationId(rs.getString("location_id"))
                    .hallId(rs.getString("hall_id"))
                    .rowNum(rs.getLong("row_num"))
                    .seatNum(rs.getLong("seat_num"))
                    .ticketCategory(TicketCategory.valueOf(rs.getString("category")))
                    .cost(rs.getBigDecimal("cost"))
                    .isSell(rs.getBoolean("is_sell"))
                    .deleted(rs.getBoolean("deleted"))
                    .build();
        }
    };

    @Override
    public Optional<Long> save(Long eventId, Ticket ticket) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(SAVE_QUERY, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, eventId);
            ps.setString(2, ticket.getLocationId());
            ps.setString(3, ticket.getHallId());
            ps.setLong(4, ticket.getRowNum());
            ps.setLong(5, ticket.getSeatNum());
            ps.setString(6, ticket.getTicketCategory().toString());
            ps.setBigDecimal(7,ticket.getCost());
            return ps;
        }, keyHolder);

        Number key = keyHolder.getKey();
        if (key == null) {
            return Optional.empty();
        }
        Long id = key.longValue();
        return Optional.of(id);
    }

    @Override
    public void batchSave(List<Ticket> tickets) {
        List<Object[]> batchParams = tickets.stream()
                .map(ticket -> new Object[]{ticket.getEventId(),
                        ticket.getLocationId(), ticket.getHallId(), ticket.getRowNum(),
                        ticket.getSeatNum(), ticket.getTicketCategory().toString(), ticket.getCost()})
                .toList();
        jdbcTemplate.batchUpdate(SAVE_QUERY, batchParams);
    }

    @Override
    public Optional<Ticket> findById(Long eventId, Long ticketId) {
        try{
            return Optional.ofNullable(jdbcTemplate.queryForObject(FIND_BY_ID_QUERY, ticketRowMapper, ticketId, eventId));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }

    }

    @Override
    public List<Ticket> findAllByEventId(Long eventId) {
        return jdbcTemplate.query(FIND_ALL_QUERY, ticketRowMapper, eventId);
    }

    @Override
    public void update(Long eventId, Long ticketId, Ticket ticket) {
        jdbcTemplate.update(UPDATE_QUERY,
                ticket.getUserId(),
                ticket.getLocationId(),
                ticket.getHallId(),
                ticket.getRowNum(),
                ticket.getSeatNum(),
                ticket.getTicketCategory().name(),
                ticket.getCost(),
                ticket.isSell(),
                ticketId,
                eventId
        );
    }

    @Override
    public void deleteById(Long eventId, Long id) {
        jdbcTemplate.update(DELETE_BY_ID_QUERY, id, eventId);
    }

    @Override
    public boolean existsById(Long eventId, Long id) {
        Boolean exists = jdbcTemplate.queryForObject(EXIST_TICKET_BY_ID_QUERY, Boolean.class, id, eventId);
        return Boolean.TRUE.equals(exists);
    }

}
