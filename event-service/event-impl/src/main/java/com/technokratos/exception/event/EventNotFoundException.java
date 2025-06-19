package com.technokratos.exception.event;

import com.technokratos.exception.EventServiceException;

public class EventNotFoundException extends EventServiceException {
    public EventNotFoundException(String message) {
        super(message);
    }
    public EventNotFoundException(String message, Long eventId) {
        super(message, eventId);
    }
}
