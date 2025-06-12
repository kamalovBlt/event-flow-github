package com.technokratos.exception;

import lombok.Getter;

@Getter
public class ExternalServiceException extends RuntimeException {

    public ExternalServiceException(String message) {
        super(message);
    }

}
