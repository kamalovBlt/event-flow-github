package com.technokratos.exception;

import lombok.Getter;

@Getter
public class UserServiceException extends RuntimeException {
    private Long userId;

    public UserServiceException(String message) {
        super(message);
    }

    public UserServiceException(String message, Long userId) {
        super(message);
        this.userId = userId;
    }

}
