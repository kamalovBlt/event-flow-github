package com.technokratos.exception.s3;

import com.technokratos.exception.EventServiceException;
import lombok.Getter;

@Getter
public class S3NotValidException extends EventServiceException {
    public S3NotValidException(String message) {
        super(message);
    }
}
