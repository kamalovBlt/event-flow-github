package com.technokratos.repository;

import com.technokratos.model.Ticket;
import com.technokratos.model.TicketCategory;
import com.technokratos.repository.interfaces.TicketRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class PostgresTicketRepositoryTest {

    @Autowired
    private TicketRepository ticketRepository;

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
                INSERT INTO event (id, name, category, location_id, hall_id, description, start_time,
                                   end_time, canceled, video_key, popularity, deleted, creator_id)
                VALUES (1, 'Jazz Night', 'CONCERT', '1', '1', 'Great jazz', '2025-01-01 19:00:00',
                        '2025-01-01 23:00:00', false, 'vid123', 75, false, 1)
        """);
    }


    @Test
    void shouldSaveNewTicket() {
        Ticket ticket = Ticket.builder()
                .eventId(1L)
                .locationId("1")
                .hallId("1")
                .rowNum(1L)
                .seatNum(10L)
                .ticketCategory(TicketCategory.VIP)
                .cost(BigDecimal.valueOf(100.0))
                .build();

        Optional<Long> savedId = ticketRepository.save(1L, ticket);
        assertThat(savedId).isPresent();

        Optional<Ticket> savedTicket = ticketRepository.findById(1L, savedId.get());
        assertThat(savedTicket).isPresent();
        assertThat(savedTicket.get().getEventId()).isEqualTo(1L);
        assertThat(savedTicket.get().getLocationId()).isEqualTo("1");
        assertThat(savedTicket.get().getHallId()).isEqualTo("1");
        assertThat(savedTicket.get().getRowNum()).isEqualTo(1L);
        assertThat(savedTicket.get().getSeatNum()).isEqualTo(10L);
        assertThat(savedTicket.get().getTicketCategory()).isEqualTo(TicketCategory.VIP);
        assertThat(savedTicket.get().getCost()).isEqualTo(BigDecimal.valueOf(100.0));
    }

    @Test
    void shouldBatchSaveTickets() {
        Ticket ticket1 = Ticket.builder()
                .eventId(1L)
                .locationId("1")
                .hallId("1")
                .rowNum(1L)
                .seatNum(10L)
                .ticketCategory(TicketCategory.VIP)
                .cost(BigDecimal.valueOf(100.0))
                .build();

        Ticket ticket2 = Ticket.builder()
                .eventId(1L)
                .locationId("1")
                .hallId("1")
                .rowNum(2L)
                .seatNum(20L)
                .ticketCategory(TicketCategory.COMMON)
                .cost(BigDecimal.valueOf(50.0))
                .build();

        List<Ticket> tickets = List.of(ticket1, ticket2);

        ticketRepository.batchSave(tickets);

        List<Ticket> savedTickets = ticketRepository.findAllByEventId(1L);
        assertThat(savedTickets).hasSize(2);
        assertThat(savedTickets.get(0).getSeatNum()).isEqualTo(10L);
        assertThat(savedTickets.get(1).getSeatNum()).isEqualTo(20L);
    }

    @Test
    void shouldFindTicketById() {
        Ticket ticket = Ticket.builder()
                .eventId(1L)
                .locationId("1")
                .hallId("1")
                .rowNum(1L)
                .seatNum(10L)
                .ticketCategory(TicketCategory.VIP)
                .cost(BigDecimal.valueOf(100.0))
                .build();

        Optional<Long> savedId = ticketRepository.save(1L, ticket);
        assertThat(savedId).isPresent();

        Optional<Ticket> foundTicket = ticketRepository.findById(1L, savedId.get());
        assertThat(foundTicket).isPresent();
        assertThat(foundTicket.get().getSeatNum()).isEqualTo(10L);
    }

    @Test
    void shouldReturnEmptyOptionalWhenTicketNotFound() {
        Optional<Ticket> ticket = ticketRepository.findById(1L, -1L);
        assertThat(ticket).isEmpty();
    }

    @Test
    void shouldFindAllTicketsByEventId() {
        Ticket ticket1 = Ticket.builder()
                .eventId(1L)
                .locationId("1")
                .hallId("1")
                .rowNum(1L)
                .seatNum(10L)
                .ticketCategory(TicketCategory.VIP)
                .cost(BigDecimal.valueOf(100.0))
                .build();

        Ticket ticket2 = Ticket.builder()
                .eventId(1L)
                .locationId("1")
                .hallId("1")
                .rowNum(2L)
                .seatNum(20L)
                .ticketCategory(TicketCategory.COMMON)
                .cost(BigDecimal.valueOf(50.0))
                .build();

        List<Ticket> tickets = List.of(ticket1, ticket2);
        ticketRepository.batchSave(tickets);

        List<Ticket> foundTickets = ticketRepository.findAllByEventId(1L);
        assertThat(foundTickets).hasSize(2);
        assertThat(foundTickets.get(0).getSeatNum()).isEqualTo(10L);
        assertThat(foundTickets.get(1).getSeatNum()).isEqualTo(20L);
    }

    @Test
    void shouldUpdateTicket() {
        Ticket ticket = Ticket.builder()
                .eventId(1L)
                .locationId("1")
                .hallId("1")
                .rowNum(1L)
                .seatNum(10L)
                .ticketCategory(TicketCategory.VIP)
                .cost(BigDecimal.valueOf(100.0))
                .build();

        Optional<Long> savedId = ticketRepository.save(1L, ticket);
        assertThat(savedId).isPresent();

        Ticket updatedTicket = Ticket.builder()
                .userId(1L)
                .eventId(1L)
                .locationId("1")
                .hallId("1")
                .rowNum(1L)
                .seatNum(10L)
                .ticketCategory(TicketCategory.COMMON)
                .cost(BigDecimal.valueOf(50.0))
                .isSell(true)
                .build();

        ticketRepository.update(1L, savedId.get(), updatedTicket);

        Optional<Ticket> foundTicket = ticketRepository.findById(1L, savedId.get());
        assertThat(foundTicket).isPresent();
        assertThat(foundTicket.get().getTicketCategory()).isEqualTo(TicketCategory.COMMON);
        assertThat(foundTicket.get().getCost()).isEqualTo(BigDecimal.valueOf(50.0));
        assertThat(foundTicket.get().isSell()).isTrue();
    }

    @Test
    void shouldSoftDeleteTicket() {
        Ticket ticket = Ticket.builder()
                .eventId(1L)
                .locationId("1")
                .hallId("1")
                .rowNum(1L)
                .seatNum(10L)
                .ticketCategory(TicketCategory.VIP)
                .cost(BigDecimal.valueOf(100.0))
                .build();

        Optional<Long> savedId = ticketRepository.save(1L, ticket);
        assertThat(savedId).isPresent();

        ticketRepository.deleteById(1L, savedId.get());

        Optional<Ticket> deletedTicket = ticketRepository.findById(1L, savedId.get());
        assertThat(deletedTicket).isEmpty();
    }

    @Test
    void shouldReturnTrueIfTicketExists() {
        Ticket ticket = Ticket.builder()
                .eventId(1L)
                .locationId("1")
                .hallId("1")
                .rowNum(1L)
                .seatNum(10L)
                .ticketCategory(TicketCategory.VIP)
                .cost(BigDecimal.valueOf(100.0))
                .build();

        Optional<Long> savedId = ticketRepository.save(1L, ticket);
        assertThat(savedId).isPresent();

        boolean exists = ticketRepository.existsById(1L, savedId.get());
        assertThat(exists).isTrue();
    }

    @Test
    void shouldReturnFalseIfTicketNotExists() {
        boolean exists = ticketRepository.existsById(1L, -1L);
        assertThat(exists).isFalse();
    }
}