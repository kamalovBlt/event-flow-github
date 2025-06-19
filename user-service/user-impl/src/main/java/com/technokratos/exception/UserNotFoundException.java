package com.technokratos.exception;

import lombok.Getter;

@Getter
public class UserNotFoundException extends UserServiceException {

    public UserNotFoundException(String message, Long userId) {
        super(message, userId);
    }

    public UserNotFoundException(String message) {
        super(message);
    }
}
