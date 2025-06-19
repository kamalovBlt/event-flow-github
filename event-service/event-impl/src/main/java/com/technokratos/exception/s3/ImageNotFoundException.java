package com.technokratos.exception.s3;

import com.technokratos.exception.EventServiceException;

public class ImageNotFoundException extends EventServiceException {
    public ImageNotFoundException(String message, Long eventId) {
        super(message, eventId);
    }
}
