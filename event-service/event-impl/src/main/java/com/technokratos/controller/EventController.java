package com.technokratos.controller;

import com.technokratos.api.EventApi;
import com.technokratos.dto.EventCategoryDTO;
import com.technokratos.dto.request.EventRequest;
import com.technokratos.dto.request.sort.SortParameterRequest;
import com.technokratos.dto.response.EventResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
public class EventController implements EventApi {

    @Override
    public List<EventResponse> find(
            SortParameterRequest sortParameter,
            LocalDateTime date1,
            LocalDateTime date2,
            List<EventCategoryDTO> categories,
            String keywords
    ) {
        return List.of();
    }

    @Override
    public EventResponse findById(Long eventId) {
        return null;
    }

    @Override
    @PreAuthorize("hasAnyAuthority('USER', 'ORGANIZER', 'PLARFORM', 'ADMIN')")
    public List<EventResponse> getRecommendations(Long userId, int page, int size) {
        return List.of();
    }

    @Override
    @PreAuthorize("hasAnyAuthority('ORGANIZER','ADMIN')")
    public Long save(EventRequest eventRequest) {
        return 0L;
    }

    @Override
    @PreAuthorize("hasAnyAuthority('ORGANIZER','ADMIN')")
    /*TODO: @PreAuthorize("(hasAuthority('ORGANIZER') and @eventService.isOwner(#id, principal)) or hasAuthority('ADMIN')")
    тут #id ID мероприятия, principal это ID пользователя, он автоматически берется
     */
    public void update(Long eventId, EventRequest eventRequest) {

    }

    @Override
    @PreAuthorize("hasAnyAuthority('ORGANIZER','ADMIN')")
    /*TODO: @PreAuthorize("(hasAuthority('ORGANIZER') and @eventService.isOwner(#id, principal)) or hasAuthority('ADMIN')")
    тут #id ID мероприятия, principal это ID пользователя, он автоматически берется
     */
    public void delete(Long eventId) {

    }
}
