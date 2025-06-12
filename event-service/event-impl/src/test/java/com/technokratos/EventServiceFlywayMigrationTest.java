package com.technokratos;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
public class EventServiceFlywayMigrationTest {

    @Autowired
    private DataSource dataSource;

    @Test
    void contextLoads() {
    }

    @Test
    void shouldHaveArtistTableWithCorrectStructure() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

        assertTrue(tableExists(jdbcTemplate, "artist"), "Таблица artist должна существовать");
        assertTrue(sequenceExists(jdbcTemplate, "artist_id"), "Sequence artist_id должен существовать");

        List<Map<String, Object>> columns = jdbcTemplate.queryForList(
                "SELECT column_name, data_type, is_nullable, column_default " +
                        "FROM information_schema.columns " +
                        "WHERE table_name = 'artist'"
        );

        assertColumnExists(columns, "id", "bigint", "NO", "nextval('artist_id'::regclass)");
        assertColumnExists(columns, "first_name", "character varying", "NO", null);
        assertColumnExists(columns, "last_name", "character varying", "NO", null);
        assertColumnExists(columns, "nickname", "character varying", "NO", null);
        assertColumnExists(columns, "description", "text", "YES", null);
        assertColumnExists(columns, "deleted", "boolean", "YES", "false");

        assertConstraintExists(jdbcTemplate, "artist", "artist_id_pk", "PRIMARY KEY");
    }

    @Test
    void shouldHaveEventCategoryTableWithCorrectStructure() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

        assertTrue(tableExists(jdbcTemplate, "event_category"), "Таблица event_category должна существовать");
        assertTrue(sequenceExists(jdbcTemplate, "event_category_id"), "Sequence event_category_id должен существовать");

        List<Map<String, Object>> columns = jdbcTemplate.queryForList(
                "SELECT column_name, data_type, is_nullable, column_default " +
                        "FROM information_schema.columns " +
                        "WHERE table_name = 'event_category'"
        );

        assertColumnExists(columns, "id", "bigint", "NO", "nextval('event_category_id'::regclass)");
        assertColumnExists(columns, "name", "character varying", "NO", null);
        assertColumnExists(columns, "deleted", "boolean", "YES", "false");

        assertConstraintExists(jdbcTemplate, "event_category", "event_category_id_pk", "PRIMARY KEY");
        assertConstraintExists(jdbcTemplate, "event_category", "event_category_name_unique", "UNIQUE");
    }

    @Test
    void shouldHaveEventTableWithCorrectStructure() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

        assertTrue(tableExists(jdbcTemplate, "event"), "Таблица event должна существовать");
        assertTrue(sequenceExists(jdbcTemplate, "event_id"), "Sequence event_id должен существовать");

        List<Map<String, Object>> columns = jdbcTemplate.queryForList(
                "SELECT column_name, data_type, is_nullable, column_default " +
                        "FROM information_schema.columns " +
                        "WHERE table_name = 'event'"
        );

        assertColumnExists(columns, "id", "bigint", "NO", "nextval('event_id'::regclass)");
        assertColumnExists(columns, "name", "character varying", "NO", null);
        assertColumnExists(columns, "category_id", "bigint", "NO", null);
        assertColumnExists(columns, "location_id", "character varying", "YES", null);
        assertColumnExists(columns, "hall_id", "bigint", "YES", null);
        assertColumnExists(columns, "description", "text", "YES", null);
        assertColumnExists(columns, "date", "timestamp without time zone", "YES", null);
        assertColumnExists(columns, "canceled", "boolean", "YES", "false");
        assertColumnExists(columns, "video_key", "character varying", "YES", null);
        assertColumnExists(columns, "popularity", "integer", "YES", "0");
        assertColumnExists(columns, "deleted", "boolean", "YES", "false");

        assertConstraintExists(jdbcTemplate, "event", "event_id_pk", "PRIMARY KEY");
        assertConstraintExists(jdbcTemplate, "event", "event_category_id_fk", "FOREIGN KEY");
    }

    @Test
    void shouldHaveEventImageTableWithCorrectStructure() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

        assertTrue(tableExists(jdbcTemplate, "event_image"), "Таблица event_image должна существовать");
        assertTrue(sequenceExists(jdbcTemplate, "event_image_id"), "Sequence event_image_id должен существовать");

        List<Map<String, Object>> columns = jdbcTemplate.queryForList(
                "SELECT column_name, data_type, is_nullable, column_default " +
                        "FROM information_schema.columns " +
                        "WHERE table_name = 'event_image'"
        );

        assertColumnExists(columns, "id", "bigint", "NO", "nextval('event_image_id'::regclass)");
        assertColumnExists(columns, "event_id", "bigint", "YES", null);
        assertColumnExists(columns, "key", "character varying", "NO", null);

        assertConstraintExists(jdbcTemplate, "event_image", "event_image_id_pk", "PRIMARY KEY");
        assertConstraintExists(jdbcTemplate, "event_image", "event_image_event_id_fk", "FOREIGN KEY");
    }

    @Test
    void shouldHaveEventArtistTableWithCorrectStructure() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

        assertTrue(tableExists(jdbcTemplate, "event_artist"), "Таблица event_artist должна существовать");

        List<Map<String, Object>> columns = jdbcTemplate.queryForList(
                "SELECT column_name, data_type, is_nullable " +
                        "FROM information_schema.columns " +
                        "WHERE table_name = 'event_artist'"
        );

        assertColumnExists(columns, "event_id", "bigint", "NO");
        assertColumnExists(columns, "artist_id", "bigint", "NO");

        assertConstraintExists(jdbcTemplate, "event_artist", "event_artist_event_id_fk", "FOREIGN KEY");
        assertConstraintExists(jdbcTemplate, "event_artist", "event_artist_artist_id_fk", "FOREIGN KEY");
        assertConstraintExists(jdbcTemplate, "event_artist", "event_artist_id_pk", "PRIMARY KEY");
    }

    @Test
    void shouldHaveEventOrganizerTableWithCorrectStructure() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

        assertTrue(tableExists(jdbcTemplate, "event_organizer"), "Таблица event_organizer должна существовать");

        List<Map<String, Object>> columns = jdbcTemplate.queryForList(
                "SELECT column_name, data_type, is_nullable " +
                        "FROM information_schema.columns " +
                        "WHERE table_name = 'event_organizer'"
        );

        assertColumnExists(columns, "event_id", "bigint", "NO");
        assertColumnExists(columns, "organizer_id", "bigint", "NO");

        assertConstraintExists(jdbcTemplate, "event_organizer", "event_organizer_event_id_fk", "FOREIGN KEY");
        assertConstraintExists(jdbcTemplate, "event_organizer", "event_organizer_id_pk", "PRIMARY KEY");
    }

    @Test
    void shouldHaveTicketTableWithCorrectStructure() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

        assertTrue(tableExists(jdbcTemplate, "ticket"), "Таблица ticket должна существовать");

        List<Map<String, Object>> columns = jdbcTemplate.queryForList(
                "SELECT column_name, data_type, is_nullable, column_default " +
                        "FROM information_schema.columns " +
                        "WHERE table_name = 'ticket'"
        );

        assertColumnExists(columns, "user_id", "bigint", "NO", null);
        assertColumnExists(columns, "event_id", "bigint", "NO", null);
        assertColumnExists(columns, "location_id", "character varying", "NO", null);
        assertColumnExists(columns, "hall_id", "bigint", "NO", null);
        assertColumnExists(columns, "row_id", "bigint", "NO", null);
        assertColumnExists(columns, "seat_id", "bigint", "NO", null);
        assertColumnExists(columns, "category_id", "bigint", "YES", null);
        assertColumnExists(columns, "cost", "numeric", "YES", null);
        assertColumnExists(columns, "deleted", "boolean", "YES", "false");

        assertConstraintExists(jdbcTemplate, "ticket", "ticket_event_id_fk", "FOREIGN KEY");
        assertConstraintExists(jdbcTemplate, "ticket", "ticket_pk", "PRIMARY KEY");
        assertConstraintExists(jdbcTemplate, "ticket", "ticket_category_fk", "FOREIGN KEY");
    }

    private boolean tableExists(JdbcTemplate jdbcTemplate, String tableName) {
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(
                """
                        SELECT EXISTS (
                            SELECT 1
                            FROM information_schema.tables
                            WHERE table_schema = 'public' AND table_name = ?
                        )""",
                Boolean.class,
                tableName
        ));
    }

    private boolean sequenceExists(JdbcTemplate jdbcTemplate, String sequenceName) {
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(
                """
                    SELECT EXISTS (
                        SELECT 1
                        FROM information_schema.sequences
                        WHERE sequence_schema = 'public' AND sequence_name = ?
                    )""",
                Boolean.class,
                sequenceName
        ));
    }

    private void assertColumnExists(List<Map<String, Object>> columns, String columnName,
                                    String dataType, String isNullable) {
        assertColumnExists(columns, columnName, dataType, isNullable, null);
    }

    private void assertColumnExists(List<Map<String, Object>> columns, String columnName,
                                    String dataType, String isNullable, String columnDefault) {
        boolean found = columns.stream()
                .anyMatch(col ->
                        col.get("column_name").equals(columnName) &&
                                col.get("data_type").equals(dataType) &&
                                col.get("is_nullable").equals(isNullable) &&
                                (columnDefault == null || (col.get("column_default") != null &&
                                        col.get("column_default").toString().contains(columnDefault)))
                );

        assertTrue(found, String.format("Колонка %s с типом %s, nullable=%s и default=%s должна существовать",
                columnName, dataType, isNullable, columnDefault));
    }

    private void assertConstraintExists(JdbcTemplate jdbcTemplate, String tableName,
                                        String constraintName, String constraintType) {
        Boolean exists = jdbcTemplate.queryForObject(
                "SELECT EXISTS (SELECT 1 FROM information_schema.table_constraints " +
                        "WHERE table_name = ? AND constraint_name = ? AND constraint_type = ?)",
                Boolean.class,
                tableName, constraintName, constraintType
        );

        assertTrue(exists, String.format("Constraint %s типа %s для таблицы %s должен существовать",
                constraintName, constraintType, tableName));
    }
}