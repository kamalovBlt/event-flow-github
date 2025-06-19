package com.technokratos.exception.event;

import com.technokratos.exception.EventServiceException;

public class EventSaveException extends EventServiceException {
    public EventSaveException(String message) {
        super(message);
    }

}
