package com.technokratos.exception;

import lombok.Getter;

@Getter
public class UserNotFoundException extends RuntimeException{

    private final String email;
    private final String firstName;
    private final String lastName;

    public UserNotFoundException(String message, String email, String firstName, String lastName){
        super(message);
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
    }
}
