package com.technokratos.exception.s3;

import com.technokratos.exception.EventServiceException;
import lombok.Getter;

@Getter
public class ImageMaxCountException extends EventServiceException {

    public ImageMaxCountException(String message, Long eventId) {
        super(message, eventId);
    }
}
