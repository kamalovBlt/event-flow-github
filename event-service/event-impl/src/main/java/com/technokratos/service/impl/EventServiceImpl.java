package com.technokratos.service.impl;

import com.technokratos.dto.request.EventRequest;
import com.technokratos.dto.response.event.EventResponse;
import com.technokratos.exception.event.EventConflictException;
import com.technokratos.exception.event.EventNotFoundException;
import com.technokratos.exception.event.EventSaveException;
import com.technokratos.mapper.api.EventMapper;
import com.technokratos.model.Event;
import com.technokratos.repository.interfaces.EventRepository;
import com.technokratos.service.interfaces.EventService;
import com.technokratos.service.interfaces.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final TicketService ticketService;
    private final EventMapper eventMapper;

    @Override
    public Long save(EventRequest eventRequest) {
        Event event = eventMapper.toEntity(eventRequest);
        if (eventRepository.existsByTimeAndLocationIdAndHallId(
                event.getLocationId(),event.getHallId(), event.getStart(),event.getEnd())) {
            throw new EventConflictException("Мероприятие в это время, в данной локации уже существует");
        }

        event.setCanceled(false);
        event.setPopularity(0);

        Long eventId = eventRepository.save(event).orElseThrow(
                () -> new EventSaveException("Ошибка при сохранении. Попробуйте позже.")
        );
        ticketService.batchSave(eventRequest.tickets());
        return eventId;
    }

    @Override
    public EventResponse findById(Long id) {
        Event event = eventRepository.findById(id).orElseThrow(
                () -> new EventNotFoundException("Событие с id %s не найдено".formatted(id))
        );
        return eventMapper.toResponse(event);
    }

    @Override
    public List<EventResponse> findAll(int page, int size) {
        return eventRepository.findAll(page, size).stream()
                .map(eventMapper::toResponse)
                .toList();
    }

    @Override
    public void update(Long id, EventRequest eventRequest) {
        if (!eventRepository.existsById(id)) {
            throw new EventNotFoundException("Мероприятие для удаления не найдено",id);
        }
        Event event = eventMapper.toEntity(eventRequest);
        event.setId(id);

        event.setCanceled(false);
        event.setPopularity(0);

        if (eventRepository.existsByTimeAndLocationIdAndHallId(
                event.getLocationId(),event.getHallId(), event.getStart(),event.getEnd())) {
            throw new EventConflictException("Мероприятие в это время, в данной локации уже существует");
        }

        eventRepository.update(event);
    }

    @Override
    public void deleteById(Long id) {
        if (!eventRepository.existsById(id)) {
            throw new EventNotFoundException("Мероприятие для удаления не найдено",id);
        }
        eventRepository.deleteById(id);
    }

    @Override
    public boolean isCreator(Long eventId, Authentication authentication) {
        Long creatorId = eventRepository.findCreatorIdByEventId(eventId).orElseThrow(
                () -> new EventNotFoundException("Мероприятие не найдено, id: %s".formatted(eventId),eventId)
        );
        Jwt jwt = (Jwt) authentication.getPrincipal();
        Long userId = jwt.getClaim("user-id");
        return creatorId.equals(userId);
    }
}
