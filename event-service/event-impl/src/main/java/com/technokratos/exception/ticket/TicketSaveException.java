package com.technokratos.exception.ticket;

import com.technokratos.exception.EventServiceException;

public class TicketSaveException extends EventServiceException {
    public TicketSaveException(String message) {
        super(message);
    }
}
