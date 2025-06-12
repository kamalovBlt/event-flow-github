package com.technokratos;

import com.technokratos.model.EventCategory;
import com.technokratos.model.Ticket;
import com.technokratos.model.TicketId;
import com.technokratos.repository.impl.TicketRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class TicketRepositoryImplTest extends RepositoryTestBase {

    @Autowired
    private TicketRepositoryImpl ticketRepository;

    @BeforeEach
    void setUp() {
        jdbcTemplate.update("INSERT INTO event_category (id, name, deleted) VALUES (1, 'Standard', false)");

        EventCategory category = new EventCategory(1L, "Standard", false);

        jdbcTemplate.update("""
            INSERT INTO event (id, name, category_id, location_id, hall_id, description, date, canceled, video_key, popularity, deleted)
            VALUES (1, 'Test Event', 1, 1, 1, 'Description', ?, false, 'video123', 100, false)
            """, LocalDateTime.now());
    }

    @Test
    void shouldSaveAndFindById() {
        Ticket ticket = createTestTicket(1L);
        TicketId ticketId = ticketRepository.save(ticket);

        assertEquals(new TicketId(1L, 1L, "123", 1L, 1L, 1L), ticketId);

        Optional<Ticket> found = ticketRepository.findById(ticketId);
        assertTrue(found.isPresent());
        assertEquals(BigDecimal.valueOf(100), found.get().getCost());
    }

    @Test
    void shouldFindByIdNotFound() {
        Optional<Ticket> found = ticketRepository.findById(
                new TicketId(999L, 999L, "123", 999L, 999L, 999L));
        assertFalse(found.isPresent());
    }

    @Test
    void shouldFindAll() {
        ticketRepository.save(createTestTicket(1L));
        ticketRepository.save(createTestTicket(2L));

        List<Ticket> tickets = ticketRepository.findAll(1, 10);
        assertEquals(2, tickets.size());
    }

    @Test
    void shouldUpdate() {
        Ticket ticket = createTestTicket(1L);
        TicketId ticketId = ticketRepository.save(ticket);

        ticket.setCost(BigDecimal.valueOf(200));
        ticketRepository.update(ticket);

        Optional<Ticket> updated = ticketRepository.findById(ticketId);
        assertTrue(updated.isPresent());
        assertEquals(BigDecimal.valueOf(200), updated.get().getCost());
    }

    @Test
    void shouldDelete() {
        Ticket ticket = createTestTicket(1L);
        ticketRepository.save(ticket);

        ticketRepository.deleteById(new TicketId(1L, 1L, "123", 1L, 1L, 1L));

        Optional<Ticket> deleted = ticketRepository.findById(
                new TicketId(1L, 1L, "123", 1L, 1L, 1L));
        assertFalse(deleted.isPresent());
    }

    private Ticket createTestTicket(Long userId) {
        Ticket ticket = new Ticket();
        ticket.setUserId(userId);
        ticket.setEventId(1L);
        ticket.setLocationId("123");
        ticket.setHallId(1L);
        ticket.setRowId(1L);
        ticket.setSeatId(1L);
        ticket.setCategoryId(1);
        ticket.setCost(BigDecimal.valueOf(100));
        ticket.setDeleted(false);
        return ticket;
    }
}