package com.technokratos.exception;

import lombok.Getter;

@Getter
public class EventServiceException extends RuntimeException {
    private Long eventId;

    public EventServiceException(String message) {
        super(message);
    }

    public EventServiceException(String message, Long eventId) {
        super(message);
        this.eventId = eventId;
    }
}