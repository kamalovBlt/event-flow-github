package com.technokratos.controller;

import com.technokratos.api.EventTicketsApi;
import com.technokratos.dto.request.ticket.TicketRequest;
import com.technokratos.dto.response.ticket.PaymentLinkResponse;
import com.technokratos.dto.response.ticket.TicketResponse;
import com.technokratos.dto.response.ticket.TicketsResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EventTicketsController implements EventTicketsApi {
    @Override
    public TicketsResponse findTickets(Long eventId) {
        return null;
    }

    @Override
    public TicketResponse findTicketById(Long eventId, Long ticketId) {
        return null;
    }

    @Override
    @PreAuthorize("hasAnyAuthority('USER', 'ORGANIZER', 'PLATFORM', 'ADMIN')")
    public PaymentLinkResponse purchase(Long eventId, Long ticketId) {
        return null;
    }

    @Override
    @PreAuthorize("hasAnyAuthority('ORGANIZER', 'ADMIN')")
    /*TODO: @PreAuthorize("(hasAuthority('ORGANIZER') and @eventService.isOwner(#id, principal)) or hasAuthority('ADMIN')")
    тут #id ID мероприятия, principal это ID пользователя, он автоматически берется
     */
    public void addTicket(Long eventId, TicketRequest ticketRequest) {

    }

    @Override
    @PreAuthorize("hasAnyAuthority('ORGANIZER', 'ADMIN')")
    /*TODO: @PreAuthorize("(hasAuthority('ORGANIZER') and @eventService.isOwner(#id, principal)) or hasAuthority('ADMIN')")
    тут #id ID мероприятия, principal это ID пользователя, он автоматически берется
     */
    public void updateTicket(Long eventId, Long ticketId, TicketRequest ticketRequest) {

    }

    @Override
    @PreAuthorize("hasAnyAuthority('ORGANIZER', 'ADMIN')")
    /*TODO: @PreAuthorize("(hasAuthority('ORGANIZER') and @eventService.isOwner(#id, principal)) or hasAuthority('ADMIN')")
    тут #id ID мероприятия, principal это ID пользователя, он автоматически берется
     */
    public void deleteTicket(Long eventId, Long ticketId) {

    }
}
