package com.technokratos.repository;

import com.technokratos.exception.event.EventNotFoundException;
import com.technokratos.model.Artist;
import com.technokratos.model.Event;
import com.technokratos.model.EventCategory;
import com.technokratos.repository.impl.PostgresArtistRepository;
import com.technokratos.repository.interfaces.ArtistRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class PostgresArtistRepositoryTest {

    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void insertTestData() {
        jdbcTemplate.update("DELETE FROM ticket");
        jdbcTemplate.update("DELETE FROM event_artist");
        jdbcTemplate.update("DELETE FROM event_image");
        jdbcTemplate.update("DELETE FROM event");
        jdbcTemplate.update("DELETE FROM artist");

        jdbcTemplate.update("""
                INSERT INTO artist (id, first_name, last_name, nickname, description, deleted, creator_id)
                VALUES (100, 'Alice', 'Smith', 'A.S.', 'Classical Artist', false, 1)
        """);
        jdbcTemplate.update("""
                INSERT INTO event (id, name, category, location_id, hall_id, description, start_time,
                                   canceled, video_key, popularity, deleted, creator_id)
                VALUES (1, 'Jazz Night', 'CONCERT', '1', '1', 'Great jazz', '2025-01-01 19:00:00', false, 'vid123', 75, false, 2)
        """);
        jdbcTemplate.update("""
                INSERT INTO event (id, name, category, location_id, hall_id, description, start_time,
                                   canceled, video_key, popularity, deleted, creator_id)
                VALUES (2, 'Rock Fest', 'CONCERT', '2', '2', 'Rock on!', '2025-02-01 20:00:00', false, 'vid456', 90, false, 2)
        """);
        jdbcTemplate.update("""
                INSERT INTO event (id, name, category, location_id, hall_id, description, start_time,
                                   canceled, video_key, popularity, deleted, creator_id)
                VALUES (3, 'Pop Night', 'CONCERT', '3', '3', 'Pop vibes', '2025-03-01 21:00:00', false, 'vid789', 85, false, 3)
        """);
        jdbcTemplate.update("INSERT INTO event_artist (event_id, artist_id) VALUES (1, 100)");
        jdbcTemplate.update("INSERT INTO event_artist (event_id, artist_id) VALUES (2, 100)");
    }

    @Test
    void shouldSaveNewArtistWithTwoLinkedEvents() {
        Event event1 = Event.builder()
                .id(1L)
                .eventCategory(EventCategory.NO_CATEGORY)
                .creatorId(1L)
                .name("La1")
                .build();
        Event event2 = Event.builder()
                .id(2L)
                .eventCategory(EventCategory.NO_CATEGORY)
                .creatorId(1L)
                .name("La2")
                .build();

        Artist artist = Artist.builder()
                .firstName("John")
                .lastName("Doe")
                .nickname("J.D.")
                .description("Jazz Artist")
                .creatorId(2L)
                .deleted(false)
                .events(List.of(event1, event2))
                .build();

        Optional<Long> savedId = artistRepository.save(artist);
        assertThat(savedId).isPresent();

        Optional<Artist> savedArtist = artistRepository.findById(savedId.get());
        assertThat(savedArtist).isPresent();
        assertThat(savedArtist.get().getEvents()).hasSize(2);
    }

    @Test
    void shouldSaveArtistWithoutEvents() {
        Artist artist = Artist.builder()
                .firstName("John")
                .lastName("Doe")
                .nickname("J.D.")
                .description("Jazz Artist")
                .creatorId(2L)
                .build();

        Optional<Long> savedId = artistRepository.save(artist);
        assertThat(savedId).isPresent();

        Optional<Artist> savedArtist = artistRepository.findById(savedId.get());
        assertThat(savedArtist).isPresent();
        assertThat(savedArtist.get().getEvents()).isEmpty();
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void shouldThrowEventNotFoundExceptionAndRollbackTransactionWhenEventIdNotFoundDuringSaveArtist() {
        Artist artist = Artist.builder()
                .firstName("Rollback")
                .lastName("Test")
                .nickname("RB")
                .description("Testing rollback")
                .creatorId(2L)
                .build();

        List<Event> invalidEvents = List.of(Event.builder().id(1L).build(), Event.builder().id(999L).build());
        artist.setEvents(invalidEvents);

        assertThatThrownBy(() -> artistRepository.save(artist))
                .isInstanceOf(EventNotFoundException.class);

        Long count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM artist WHERE first_name = ? AND last_name = ?",
                Long.class, "Rollback", "Test");
        assertThat(count).isEqualTo(0);
    }

    @Test
    void shouldFindArtistByIdWithEvents() {
        Optional<Artist> artist = artistRepository.findById(100L);
        assertThat(artist).isPresent();
        assertThat(artist.get().getEvents()).isNotEmpty();
    }

    @Test
    void shouldReturnEmptyOptionalWhenArtistNotFound() {
        Optional<Artist> artist = artistRepository.findById(-1L);
        assertThat(artist).isEmpty();
    }

    @Test
    void shouldFindAllArtistsWithEventsWithPaginationWherePage0Size10() {
        List<Artist> artists = artistRepository.findAll(0, 10);
        assertThat(artists).isNotEmpty();
        for (Artist artist : artists) {
            assertThat(artist.getEvents()).isNotEmpty();
        }
    }

    @Test
    void shouldUpdateArtistWithId100AndReplaceThreeEventLinks() {
        Artist artist = Artist.builder()
                .id(100L)
                .firstName("Updated")
                .lastName("Artist")
                .nickname("U.A.")
                .description("Updated Desc")
                .creatorId(2L)
                .events(List.of(
                        Event.builder().id(1L).build(),
                        Event.builder().id(2L).build(),
                        Event.builder().id(3L).build()
                ))
                .build();

        artistRepository.update(artist);

        Optional<Artist> updated = artistRepository.findById(100L);
        assertThat(updated).isPresent();
        assertThat(updated.get().getFirstName()).isEqualTo("Updated");
        assertThat(updated.get().getEvents()).hasSize(3);
        List<Event> events = updated.get().getEvents();
        for (Event event : events) {
            assertThat(event.getId()).isIn(1L, 2L, 3L);
        }
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void shouldThrowEventNotFoundExceptionAndRollbackTransactionWhenEventIdNotFoundDuringUpdate() {
        Artist artist = Artist.builder()
                .id(100L)
                .firstName("Rollback")
                .lastName("Test")
                .nickname("RB")
                .description("Testing rollback")
                .creatorId(2L)
                .events(List.of(Event.builder().id(999L).build()))
                .build();

        assertThatThrownBy(() -> artistRepository.update(artist))
                .isInstanceOf(EventNotFoundException.class);

        Optional<Artist> original = artistRepository.findById(100L);
        assertThat(original).isPresent();
        assertThat(original.get().getFirstName()).isEqualTo("Alice");
        assertThat(original.get().getLastName()).isEqualTo("Smith");
        assertThat(original.get().getEvents()).hasSize(2);
    }

    @Test
    void shouldSoftDeleteArtistAndRemoveLinks() {
        artistRepository.deleteById(100L);
        Optional<Artist> deleted = artistRepository.findById(100L);
        assertThat(deleted).isEmpty();

        Long linkCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM event_artist WHERE artist_id = ?", Long.class, 100L);
        assertThat(linkCount).isEqualTo(0);
    }

    @Test
    void shouldReturnTrueIfArtistExistsAndNotDeleted() {
        boolean exists = artistRepository.existsById(100L);
        assertThat(exists).isTrue();
    }

    @Test
    void shouldReturnFalseIfArtistNotExists() {
        boolean exists = artistRepository.existsById(999L);
        assertThat(exists).isFalse();
    }

    @Test
    void shouldReturnFalseIfArtistDeleted() {
        artistRepository.deleteById(100L);
        boolean exists = artistRepository.existsById(100L);
        assertThat(exists).isFalse();
    }
    @Test
    void shouldFindArtistsByKeywordsWithPagination() {
        List<Artist> artists = artistRepository.findAll("Alice", 0, 10);
        assertThat(artists).isNotEmpty();
        for (Artist artist : artists) {
            assertThat(artist.getFirstName()).containsIgnoringCase("Alice");
        }
    }

    @Test
    void shouldReturnEmptyListWhenNoArtistsMatchKeywords() {
        List<Artist> artists = artistRepository.findAll("NonExistentKeyword", 0, 10);
        assertThat(artists).isEmpty();
    }

    @Test
    void shouldReturnCreatorIdForExistingArtist() {
        Optional<Long> creatorId = artistRepository.findCreatorIdByArtistId(100L);
        assertThat(creatorId).isPresent();
        assertThat(creatorId.get()).isEqualTo(1L);
    }

    @Test
    void shouldReturnEmptyOptionalForNonExistentArtist() {
        Optional<Long> creatorId = artistRepository.findCreatorIdByArtistId(999L);
        assertThat(creatorId).isEmpty();
    }

    @Test
    void shouldReturnTrueIfNicknameExists() {
        boolean exists = artistRepository.existsByNickname("A.S.");
        assertThat(exists).isTrue();
    }

    @Test
    void shouldReturnFalseIfNicknameDoesNotExist() {
        boolean exists = artistRepository.existsByNickname("NonExistentNickname");
        assertThat(exists).isFalse();
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void shouldThrowEventNotFoundExceptionWhenEventDoesNotExist() {
        Artist artist = Artist.builder()
                .firstName("Test")
                .lastName("Artist")
                .nickname("T.A.")
                .description("Testing events validation")
                .creatorId(2L)
                .events(List.of(Event.builder().id(999L).build()))
                .build();

        assertThatThrownBy(() -> artistRepository.save(artist))
                .isInstanceOf(EventNotFoundException.class);

        Long count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM artist WHERE first_name = ? AND last_name = ?",
                Long.class, "Test", "Artist");
        assertThat(count).isEqualTo(0);
    }

    @Test
    void shouldNotThrowExceptionWhenAllEventsExist() {
        Artist artist = Artist.builder()
                .firstName("Valid")
                .lastName("Artist")
                .nickname("V.A.")
                .description("Valid events")
                .creatorId(2L)
                .events(List.of(Event.builder().id(1L).build(), Event.builder().id(2L).build()))
                .build();

        Optional<Long> savedId = artistRepository.save(artist);
        assertThat(savedId).isPresent();

        Optional<Artist> savedArtist = artistRepository.findById(savedId.get());
        assertThat(savedArtist).isPresent();
        assertThat(savedArtist.get().getEvents()).hasSize(2);
    }

    @Test
    void shouldHandleNullEventsGracefully() {
        Artist artist = Artist.builder()
                .firstName("NoEvents")
                .lastName("Artist")
                .nickname("N.E.")
                .description("No events linked")
                .creatorId(2L)
                .build();

        Optional<Long> savedId = artistRepository.save(artist);
        assertThat(savedId).isPresent();

        Optional<Artist> savedArtist = artistRepository.findById(savedId.get());
        assertThat(savedArtist).isPresent();
        assertThat(savedArtist.get().getEvents()).isEmpty();
    }
    @Test
    void shouldUpdateArtistAndRemoveAllEvents() {
        Artist artist = Artist.builder()
                .id(100L)
                .firstName("UpdateNoEvents")
                .lastName("Artist")
                .nickname("N.E.")
                .description("No events linked")
                .creatorId(2L)
                .events(List.of())
                .build();

        artistRepository.update(artist);

        Optional<Artist> updated = artistRepository.findById(100L);
        assertThat(updated).isPresent();
        assertThat(updated.get().getEvents()).isEmpty();

        Long linkCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM event_artist WHERE artist_id = ?", Long.class, 1L);
        assertThat(linkCount).isEqualTo(0);
    }
}