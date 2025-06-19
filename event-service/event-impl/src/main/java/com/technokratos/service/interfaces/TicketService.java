package com.technokratos.service.interfaces;

import com.technokratos.dto.request.ticket.TicketFullRequest;
import com.technokratos.dto.request.ticket.TicketsRequest;
import com.technokratos.dto.response.ticket.TicketFullResponse;
import com.technokratos.dto.response.ticket.TicketsResponse;

public interface TicketService {
    Long save(Long eventId, TicketFullRequest ticketFullRequest);
    void batchSave(TicketsRequest ticketsRequest);
    TicketFullResponse findById(Long eventId, Long ticketId);
    TicketsResponse findAllByEventId(Long eventId);
    void update(Long eventId, Long ticketId, TicketFullRequest ticketFullRequest);
    void deleteById(Long eventId, Long id);
}
