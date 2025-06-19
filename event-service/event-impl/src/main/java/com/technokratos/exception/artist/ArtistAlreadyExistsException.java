package com.technokratos.exception.artist;

import com.technokratos.exception.EventServiceException;

public class ArtistAlreadyExistsException extends EventServiceException {
    public ArtistAlreadyExistsException(String message) {
        super(message);
    }
}
