package com.technokratos.exception.event;

import com.technokratos.exception.EventServiceException;

public class EventConflictException extends EventServiceException {
    public EventConflictException(String message) {
        super(message);
    }
}
