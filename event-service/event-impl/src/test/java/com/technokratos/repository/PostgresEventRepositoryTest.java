package com.technokratos.repository;

import com.technokratos.exception.artist.ArtistNotFoundException;
import com.technokratos.model.Artist;
import com.technokratos.model.Event;
import com.technokratos.model.EventCategory;
import com.technokratos.repository.interfaces.EventRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class PostgresEventRepositoryTest {

    @Autowired
    private EventRepository eventRepository;

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
                INSERT INTO artist (id, first_name, last_name, nickname, description, deleted, creator_id)
                VALUES (200, 'Bob', 'Johnson', 'B.J.', 'Rock Artist', false, 2)
        """);

        jdbcTemplate.update("""
                INSERT INTO event (id, name, category, location_id, hall_id, description, start_time,
                                   end_time, canceled, video_key, popularity, deleted, creator_id)
                VALUES (1, 'Jazz Night', 'CONCERT', '1', '1', 'Great jazz', '2025-01-01 19:00:00',
                        '2025-01-01 23:00:00', false, 'vid123', 75, false, 1)
        """);
        jdbcTemplate.update("""
                INSERT INTO event (id, name, category, location_id, hall_id, description, start_time,
                                   end_time, canceled, video_key, popularity, deleted, creator_id)
                VALUES (2, 'Rock Fest', 'CONCERT', '2', '2', 'Rock on!', '2025-02-01 20:00:00',
                        '2025-02-01 23:00:00', false, 'vid456', 90, false, 2)
        """);
        jdbcTemplate.update("INSERT INTO event_artist (event_id, artist_id) VALUES (1, 100)");
        jdbcTemplate.update("INSERT INTO event_artist (event_id, artist_id) VALUES (2, 200)");
    }


    @Test
    void shouldSaveNewEventWithTwoLinkedArtists() {
        Artist artist1 = Artist.builder()
                .id(100L)
                .firstName("Alice")
                .lastName("Smith")
                .nickname("A.S.")
                .build();
        Artist artist2 = Artist.builder()
                .id(200L)
                .firstName("Bob")
                .lastName("Johnson")
                .nickname("B.J.")
                .build();

        Event event = Event.builder()
                .name("New Event")
                .eventCategory(EventCategory.CONCERT)
                .locationId("3")
                .hallId("3")
                .description("New event description")
                .start(LocalDateTime.of(2025, 3, 1, 20, 0))
                .end(LocalDateTime.of(2025, 3, 1, 23, 0))
                .canceled(false)
                .videoKey("newVideoKey")
                .popularity(80)
                .creatorId(1L)
                .artists(List.of(artist1, artist2))
                .build();

        Optional<Long> savedId = eventRepository.save(event);
        assertThat(savedId).isPresent();

        Optional<Event> savedEvent = eventRepository.findById(savedId.get());
        assertThat(savedEvent).isPresent();
        assertThat(savedEvent.get().getArtists()).hasSize(2);
    }

    @Test
    void shouldSaveEventWithoutArtists() {
        Event event = Event.builder()
                .name("Solo Event")
                .eventCategory(EventCategory.CONCERT)
                .locationId("4")
                .hallId("4")
                .description("Solo event description")
                .start(LocalDateTime.of(2025, 4, 1, 20, 0))
                .end(LocalDateTime.of(2025, 4, 1, 23, 0))
                .canceled(false)
                .videoKey("soloVideoKey")
                .popularity(60)
                .creatorId(2L)
                .build();

        Optional<Long> savedId = eventRepository.save(event);
        assertThat(savedId).isPresent();

        Optional<Event> savedEvent = eventRepository.findById(savedId.get());
        assertThat(savedEvent).isPresent();
        assertThat(savedEvent.get().getArtists()).isEmpty();
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void shouldThrowArtistNotFoundExceptionAndRollbackTransactionWhenArtistIdNotFoundDuringSaveEvent() {
        Event event = Event.builder()
                .name("Invalid Event")
                .eventCategory(EventCategory.CONCERT)
                .locationId("5")
                .hallId("5")
                .description("Invalid event description")
                .start(LocalDateTime.of(2025, 5, 1, 20, 0))
                .end(LocalDateTime.of(2025, 5, 1, 23, 0))
                .canceled(false)
                .videoKey("invalidVideoKey")
                .popularity(50)
                .creatorId(1L)
                .artists(List.of(Artist.builder().id(999L).build()))
                .build();

        assertThatThrownBy(() -> eventRepository.save(event))
                .isInstanceOf(ArtistNotFoundException.class);

        Long count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM event WHERE name = ?", Long.class, "Invalid Event");
        assertThat(count).isEqualTo(0);
    }

    @Test
    void shouldFindEventByIdWithArtists() {
        Optional<Event> event = eventRepository.findById(1L);
        assertThat(event).isPresent();
        assertThat(event.get().getArtists()).isNotEmpty();
    }

    @Test
    void shouldReturnEmptyOptionalWhenEventNotFound() {
        Optional<Event> event = eventRepository.findById(-1L);
        assertThat(event).isEmpty();
    }

    @Test
    void shouldFindAllEventsWithPagination() {
        List<Event> events = eventRepository.findAll(0, 10);
        assertThat(events).isNotEmpty();
        for (Event event : events) {
            assertThat(event.getArtists()).isNotEmpty();
        }
    }

    @Test
    void shouldUpdateEventWithId1AndReplaceArtists() {
        Artist artist1 = Artist.builder()
                .id(100L)
                .firstName("Alice")
                .lastName("Smith")
                .nickname("A.S.")
                .build();
        Artist artist2 = Artist.builder()
                .id(200L)
                .firstName("Bob")
                .lastName("Johnson")
                .nickname("B.J.")
                .build();

        Event event = Event.builder()
                .id(1L)
                .name("Updated Event")
                .eventCategory(EventCategory.CONCERT)
                .locationId("1")
                .hallId("1")
                .description("Updated description")
                .start(LocalDateTime.of(2025, 1, 1, 20, 0))
                .end(LocalDateTime.of(2025, 1, 1, 23, 0))
                .canceled(true)
                .videoKey("updatedVideoKey")
                .popularity(85)
                .creatorId(2L)
                .artists(List.of(artist1, artist2))
                .build();

        eventRepository.update(event);

        Optional<Event> updated = eventRepository.findById(1L);
        assertThat(updated).isPresent();
        assertThat(updated.get().getName()).isEqualTo("Updated Event");
        assertThat(updated.get().getArtists()).hasSize(2);
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void shouldThrowArtistNotFoundExceptionAndRollbackTransactionWhenArtistIdNotFoundDuringUpdate() {
        Event event = Event.builder()
                .id(1L)
                .name("Invalid Update")
                .eventCategory(EventCategory.CONCERT)
                .locationId("1")
                .hallId("1")
                .description("Invalid update description")
                .start(LocalDateTime.of(2025, 1, 1, 20, 0))
                .end(LocalDateTime.of(2025, 1, 1, 23, 0))
                .canceled(false)
                .videoKey("invalidUpdateVideoKey")
                .popularity(70)
                .creatorId(1L)
                .artists(List.of(Artist.builder().id(999L).build()))
                .build();

        assertThatThrownBy(() -> eventRepository.update(event))
                .isInstanceOf(ArtistNotFoundException.class);

        Optional<Event> original = eventRepository.findById(1L);
        assertThat(original).isPresent();
        assertThat(original.get().getName()).isEqualTo("Jazz Night");
        assertThat(original.get().getArtists()).hasSize(1);
    }

    @Test
    void shouldSoftDeleteEventAndRemoveLinks() {
        eventRepository.deleteById(1L);

        Optional<Event> deleted = eventRepository.findById(1L);
        assertThat(deleted).isEmpty();

        Long linkCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM event_artist WHERE event_id = ?", Long.class, 1L);
        assertThat(linkCount).isEqualTo(0);
    }

    @Test
    void shouldReturnTrueIfEventExistsAndNotDeleted() {
        boolean exists = eventRepository.existsById(1L);
        assertThat(exists).isTrue();
    }

    @Test
    void shouldReturnFalseIfEventNotExists() {
        boolean exists = eventRepository.existsById(999L);
        assertThat(exists).isFalse();
    }

    @Test
    void shouldReturnFalseIfEventDeleted() {
        eventRepository.deleteById(1L);
        boolean exists = eventRepository.existsById(1L);
        assertThat(exists).isFalse();
    }

    @Test
    void shouldReturnCreatorIdForExistingEvent() {
        Optional<Long> creatorId = eventRepository.findCreatorIdByEventId(1L);
        assertThat(creatorId).isPresent();
        assertThat(creatorId.get()).isEqualTo(1L);
    }

    @Test
    void shouldReturnEmptyOptionalForNonExistentEvent() {
        Optional<Long> creatorId = eventRepository.findCreatorIdByEventId(999L);
        assertThat(creatorId).isEmpty();
    }

    @Test
    void shouldReturnTrueIfEventExistsByTimeAndLocation() {
        boolean exists = eventRepository.existsByTimeAndLocationIdAndHallId(
                "1", "1", LocalDateTime.of(2025, 1, 1, 20, 0), LocalDateTime.of(2025, 1, 1, 23, 0));
        assertThat(exists).isTrue();
    }

    @Test
    void shouldReturnFalseIfEventDoesNotExistByTimeAndLocation() {
        boolean exists = eventRepository.existsByTimeAndLocationIdAndHallId(
                "999", "999", LocalDateTime.of(2025, 1, 1, 20, 0), LocalDateTime.of(2025, 1, 1, 23, 0));
        assertThat(exists).isFalse();
    }

    @Test
    void shouldUpdateEventAndReplaceArtists() {
        Artist artist1 = Artist.builder()
                .id(100L)
                .firstName("Alice")
                .lastName("Smith")
                .nickname("A.S.")
                .build();
        Artist artist2 = Artist.builder()
                .id(200L)
                .firstName("Bob")
                .lastName("Johnson")
                .nickname("B.J.")
                .build();

        Event event = Event.builder()
                .id(1L)
                .name("Updated Event")
                .eventCategory(EventCategory.CONCERT)
                .locationId("1")
                .hallId("1")
                .description("Updated description")
                .start(LocalDateTime.of(2025, 1, 1, 20, 0))
                .end(LocalDateTime.of(2025, 1, 1, 23, 0))
                .canceled(false)
                .videoKey("updatedVideoKey")
                .popularity(85)
                .creatorId(2L)
                .artists(List.of(artist1, artist2))
                .build();

        eventRepository.update(event);

        Optional<Event> updated = eventRepository.findById(1L);
        assertThat(updated).isPresent();
        assertThat(updated.get().getArtists()).hasSize(2);
        List<Artist> artists = updated.get().getArtists();
        for (Artist artist : artists) {
            assertThat(artist.getId()).isIn(100L, 200L);
        }

        Long oldLinkCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM event_artist WHERE event_id = ? AND artist_id NOT IN (?, ?)",
                Long.class, 1L, 100L, 200L);
        assertThat(oldLinkCount).isEqualTo(0);
    }

    @Test
    void shouldUpdateEventAndRemoveAllArtists() {
        Event event = Event.builder()
                .id(1L)
                .name("Updated Event")
                .eventCategory(EventCategory.CONCERT)
                .locationId("1")
                .hallId("1")
                .description("Updated description")
                .start(LocalDateTime.of(2025, 1, 1, 20, 0))
                .end(LocalDateTime.of(2025, 1, 1, 23, 0))
                .canceled(false)
                .videoKey("updatedVideoKey")
                .popularity(85)
                .creatorId(2L)
                .artists(null)
                .build();

        eventRepository.update(event);

        Optional<Event> updated = eventRepository.findById(1L);
        assertThat(updated).isPresent();
        assertThat(updated.get().getArtists()).isEmpty();

        Long linkCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM event_artist WHERE event_id = ?", Long.class, 1L);
        assertThat(linkCount).isEqualTo(0);
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void shouldThrowArtistNotFoundExceptionWhenInvalidArtistIdProvidedDuringUpdate() {
        Event event = Event.builder()
                .id(1L)
                .name("Invalid Update")
                .eventCategory(EventCategory.CONCERT)
                .locationId("1")
                .hallId("1")
                .description("Invalid update description")
                .start(LocalDateTime.of(2025, 1, 1, 20, 0))
                .end(LocalDateTime.of(2025, 1, 1, 23, 0))
                .canceled(false)
                .videoKey("invalidUpdateVideoKey")
                .popularity(70)
                .creatorId(1L)
                .artists(List.of(Artist.builder().id(999L).build()))
                .build();

        assertThatThrownBy(() -> eventRepository.update(event))
                .isInstanceOf(ArtistNotFoundException.class);

        Optional<Event> original = eventRepository.findById(1L);
        assertThat(original).isPresent();
        assertThat(original.get().getName()).isEqualTo("Jazz Night");
        assertThat(original.get().getArtists()).hasSize(1);
    }

}