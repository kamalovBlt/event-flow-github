package com.technokratos.controller;

import com.technokratos.api.EventApi;
import com.technokratos.dto.EventCategoryDTO;
import com.technokratos.dto.request.EventRequest;
import com.technokratos.dto.request.sort.SortParameterRequest;
import com.technokratos.dto.response.event.EventResponse;
import com.technokratos.service.interfaces.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class EventController implements EventApi {

    private final EventService eventService;

    @Override
    @Validated
    public List<EventResponse> find(
            @Valid SortParameterRequest sortParameter,
            LocalDateTime date1, LocalDateTime date2,
            @Valid List<EventCategoryDTO> categories,
            String keywords,
            int page, int size) {
        return List.of();
    }

    @Override
    public EventResponse findById(Long eventId) {
        return eventService.findById(eventId);
    }

    @Override
    @PreAuthorize("hasAnyAuthority('USER', 'ORGANIZER', 'PLARFORM', 'ADMIN')")
    public List<EventResponse> getRecommendations(Long userId, int page, int size) {
        return List.of();
    }

    @Override
    @PreAuthorize("hasAnyAuthority('ORGANIZER','ADMIN')")
    @Validated
    public Long save(@Valid EventRequest eventRequest) {
        return eventService.save(eventRequest);
    }

    @Override
    @PreAuthorize("(isAuthenticated() and hasAuthority('ORGANIZER') and @eventServiceImpl.isCreator(#eventId, authentication)) or hasAuthority('ADMIN')")
    @Validated
    public void update(Long eventId, @Valid EventRequest eventRequest) {
        eventService.update(eventId, eventRequest);
    }

    @Override
    @PreAuthorize("(isAuthenticated() and hasAuthority('ORGANIZER') and @eventServiceImpl.isCreator(#eventId, authentication)) or hasAuthority('ADMIN')")
    public void delete(Long eventId) {
        eventService.deleteById(eventId);
    }
}
