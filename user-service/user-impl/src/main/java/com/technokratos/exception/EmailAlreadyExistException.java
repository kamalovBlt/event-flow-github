package com.technokratos.exception;

import lombok.Getter;

@Getter
public class EmailAlreadyExistException extends UserServiceException {
    public EmailAlreadyExistException(String message, Long userId) {
        super(message, userId);
    }
    public EmailAlreadyExistException(String message) {
        super(message);
    }
}
