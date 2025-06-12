package com.technokratos.repository.impl;

import com.technokratos.model.EventCategory;
import com.technokratos.repository.interfaces.EventCategoryRepository;
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
public class EventCategoryRepositoryImpl implements EventCategoryRepository {
    private final JdbcTemplate jdbcTemplate;

    private static final String SAVE_QUERY = """
            INSERT INTO event_category (name) VALUES (?)
            RETURNING id
            """;

    private static final String FIND_BY_ID_QUERY = """
            SELECT id, name, deleted
            FROM event_category
            WHERE id = ? AND deleted = false
            """;

    private static final String FIND_ALL_QUERY = """
            SELECT id, name, deleted FROM event_category
            WHERE deleted = false
            ORDER BY id
            LIMIT ? OFFSET ?
            """;

    private static final String UPDATE_QUERY = """
            UPDATE event_category SET name = ? WHERE id = ?
            """;

    private static final String DELETE_BY_ID_QUERY = """
            UPDATE event_category SET deleted = true WHERE id = ?
            """;

    public EventCategoryRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public long save(EventCategory category) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(con -> {
            var ps = con.prepareStatement(SAVE_QUERY, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, category.getName());
            return ps;
                }, keyHolder
        );

        Number key = keyHolder.getKey();

        return key != null ? key.longValue() : -1;

    }

    @Override
    public Optional<EventCategory> findById(Long id) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(FIND_BY_ID_QUERY, mapper, id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<EventCategory> findAll(int page, int size) {
        int offset = (page - 1) * size;
        return jdbcTemplate.query(FIND_ALL_QUERY, mapper, size, offset);
    }

    @Override
    public void update(EventCategory category) {
        jdbcTemplate.update(UPDATE_QUERY,
                category.getName(), category.getId());
    }

    @Override
    public void deleteById(Long id) {
        jdbcTemplate.update(DELETE_BY_ID_QUERY, id);
    }

    private final RowMapper<EventCategory> mapper = (rs, rowNum) ->
        EventCategory.builder().id(rs.getLong("id"))
                .name(rs.getString("name"))
                .deleted(rs.getBoolean("deleted"))
                .build();
}
