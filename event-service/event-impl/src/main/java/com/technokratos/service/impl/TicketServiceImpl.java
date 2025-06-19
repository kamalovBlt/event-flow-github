package com.technokratos.service.impl;

import com.technokratos.dto.request.ticket.TicketFullRequest;
import com.technokratos.dto.request.ticket.TicketsRequest;
import com.technokratos.dto.response.ticket.TicketFullResponse;
import com.technokratos.dto.response.ticket.TicketsResponse;
import com.technokratos.exception.event.EventNotFoundException;

import com.technokratos.exception.ticket.TicketNotFoundException;
import com.technokratos.exception.ticket.TicketSaveException;
import com.technokratos.mapper.api.ticket.TicketFullMapper;
import com.technokratos.mapper.api.ticket.TicketsMapper;
import com.technokratos.model.Ticket;
import com.technokratos.repository.interfaces.EventRepository;
import com.technokratos.repository.interfaces.TicketRepository;
import com.technokratos.service.interfaces.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;
    private final EventRepository eventRepository;
    private final TicketFullMapper ticketFullMapper;
    private final TicketsMapper ticketsMapper;

    @Override
    public Long save(Long eventId, TicketFullRequest ticketFullRequest) {
        if (!eventRepository.existsById(eventId)) {
            throw new EventNotFoundException("Не найдено мероприятие для сохранения билета",eventId);
        }
        return ticketRepository.save(eventId,ticketFullMapper.toEntity(ticketFullRequest)).orElseThrow(
                () -> new TicketSaveException("Ошибка сохранения билета. Попробуйте позже")
        );
    }

    @Override
    public void batchSave(TicketsRequest ticketsRequest) {
        ticketRepository.batchSave(ticketsMapper.toEntity(ticketsRequest));
    }

    @Override
    public TicketFullResponse findById(Long eventId, Long ticketId) {
        if (!eventRepository.existsById(eventId)) {
            throw new EventNotFoundException("Мероприятие не найдено",eventId);
        }
        Ticket ticket = ticketRepository.findById(eventId,ticketId).orElseThrow(
                () -> new TicketNotFoundException("Билет на данное мероприятие не найден",eventId)
        );
        return ticketFullMapper.toResponse(ticket);
    }

    @Override
    public TicketsResponse findAllByEventId(Long eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new EventNotFoundException("Мероприятие не найдено",eventId);
        }
        List<Ticket> tickets = ticketRepository.findAllByEventId(eventId);
        return ticketsMapper.toResponse(tickets);
    }

    @Override
    public void update(Long eventId, Long ticketId, TicketFullRequest ticketFullRequest) {
        if (!eventRepository.existsById(eventId)) {
            throw new EventNotFoundException("Мероприятие не найдено",eventId);
        }
        if (!ticketRepository.existsById(eventId, ticketId)) {
            throw new TicketNotFoundException("Билет для обновления не найден",eventId);
        }
        ticketRepository.update(eventId,ticketId,ticketFullMapper.toEntity(ticketFullRequest));
    }

    @Override
    public void deleteById(Long eventId, Long id) {
        if (!eventRepository.existsById(eventId)) {
            throw new EventNotFoundException("Мероприятие не найдено",eventId);
        }
        if (!ticketRepository.existsById(eventId, id)) {
            throw new TicketNotFoundException("Билет для удаления не найден",eventId);
        }
        ticketRepository.deleteById(eventId,id);
    }
}
