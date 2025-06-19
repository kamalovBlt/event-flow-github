package com.technokratos.repository.interfaces;

import com.technokratos.model.Ticket;

import java.util.List;
import java.util.Optional;

public interface TicketRepository {

    Optional<Long> save(Long eventId, Ticket ticket);
    void batchSave(List<Ticket> tickets);
    Optional<Ticket> findById(Long eventId, Long ticketId);
    List<Ticket> findAllByEventId(Long eventId);
    void update(Long eventId, Long ticketId, Ticket ticket);
    void deleteById(Long eventId, Long id);
    boolean existsById(Long eventId, Long id);

}
