package com.technokratos.mapper.api;

import com.technokratos.dto.request.EventRequest;
import com.technokratos.dto.response.event.EventResponse;
import com.technokratos.model.Event;

public interface EventMapper extends Mapper<EventRequest, Event, EventResponse> {
}
