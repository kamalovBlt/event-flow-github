package com.technokratos.controller;

import com.technokratos.api.EventTicketsApi;
import com.technokratos.dto.request.ticket.TicketFullRequest;
import com.technokratos.dto.response.ticket.PaymentLinkResponse;
import com.technokratos.dto.response.ticket.TicketFullResponse;
import com.technokratos.dto.response.ticket.TicketsResponse;
import com.technokratos.service.interfaces.TicketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class EventTicketsController implements EventTicketsApi {

    private final TicketService ticketService;

    @Override
    public TicketsResponse findTickets(Long eventId) {
        return ticketService.findAllByEventId(eventId);
    }

    @Override
    public TicketFullResponse findTicketById(Long eventId, Long ticketId) {
        return ticketService.findById(eventId, ticketId);
    }

    @Override
    @PreAuthorize("hasAuthority('USER')")
    public PaymentLinkResponse purchase(Long eventId, Long ticketId) {
        return null;
    }

    @Override
    @PreAuthorize("(isAuthenticated() and hasAuthority('ORGANIZER') and @eventServiceImpl.isCreator(#eventId, authentication)) or hasAuthority('ADMIN')")
    @Validated
    public Long addTicket(Long eventId, @Valid TicketFullRequest ticketFullRequest) {
        return ticketService.save(eventId, ticketFullRequest);
    }

    @Override
    @PreAuthorize("(isAuthenticated() and hasAuthority('ORGANIZER') and @eventServiceImpl.isCreator(#eventId, authentication)) or hasAuthority('ADMIN')")
    @Validated
    public void updateTicket(Long eventId, Long ticketId, @Valid TicketFullRequest ticketFullRequest) {
        ticketService.update(eventId, ticketId, ticketFullRequest);
    }

    @Override
    @PreAuthorize("(isAuthenticated() and hasAuthority('ORGANIZER') and @eventServiceImpl.isCreator(#eventId, authentication)) or hasAuthority('ADMIN')")
    public void deleteTicket(Long eventId, Long ticketId) {
        ticketService.deleteById(eventId, ticketId);
    }

}
