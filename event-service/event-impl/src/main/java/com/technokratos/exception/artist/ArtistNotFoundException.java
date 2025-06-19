package com.technokratos.exception.artist;

import com.technokratos.exception.EventServiceException;
import lombok.Getter;

@Getter
public class ArtistNotFoundException extends EventServiceException {

    public ArtistNotFoundException(String message) {
        super(message);
    }
}
