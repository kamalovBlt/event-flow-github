package com.technokratos.exception;

import lombok.Getter;

@Getter
public class LocationNotFoundException extends RuntimeException {
    private final String locationId;

    public LocationNotFoundException(String locationId) {
        super("Location with id %s not found".formatted(locationId));
        this.locationId = locationId;
    }
}