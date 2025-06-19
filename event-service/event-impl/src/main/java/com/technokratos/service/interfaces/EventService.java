package com.technokratos.service.interfaces;

import com.technokratos.dto.request.EventRequest;
import com.technokratos.dto.response.event.EventResponse;
import org.springframework.security.core.Authentication;

import java.util.List;


public interface EventService {
    Long save(EventRequest eventRequest);
    EventResponse findById(Long id);
    List<EventResponse> findAll(int page, int size);
    void update(Long id, EventRequest eventRequest);
    void deleteById(Long id);
    boolean isCreator(Long eventId, Authentication authentication);
}
