package com.technokratos.exception.s3;

import com.technokratos.exception.EventServiceException;

public class S3LoadException extends EventServiceException {
    public S3LoadException(String message) {
        super(message);
    }
}
