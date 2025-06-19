package com.technokratos.exception.ticket;

import com.technokratos.exception.EventServiceException;

public class TicketNotFoundException extends EventServiceException {
    public TicketNotFoundException(String message, Long eventId) {
        super(message, eventId);
    }
}
