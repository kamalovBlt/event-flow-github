package com.technokratos.exception.s3;

import com.technokratos.exception.EventServiceException;
import lombok.Getter;

@Getter
public class S3MediaNotFoundException extends EventServiceException {

    public S3MediaNotFoundException(String message, Long eventId) {
        super(message, eventId);
    }
}
