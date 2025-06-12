package com.technokratos.repository.interfaces;

import com.technokratos.model.Ticket;
import com.technokratos.model.TicketId;

import java.util.List;
import java.util.Optional;

public interface TicketRepository {
    TicketId save(Ticket ticket);

    Optional<Ticket> findById(TicketId id);

    List<Ticket> findAll(int page, int size);

    void update(Ticket ticket);

    void deleteById(TicketId id);
}
