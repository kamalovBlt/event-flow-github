package com.technokratos.controller;

import com.technokratos.api.LocationApi;
import com.technokratos.dto.request.LocationRequest;
import com.technokratos.dto.response.LocationResponse;
import com.technokratos.dto.response.LocationShortResponse;
import com.technokratos.service.api.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Validated
@RequiredArgsConstructor
public class LocationController implements LocationApi {

    private final LocationService locationService;

    @Override
    @PreAuthorize("hasAnyAuthority('ORGANIZER', 'PLATFORM', 'ADMIN')")
    public LocationResponse findById(String id) {
        return locationService.findById(id);
    }

    @Override
    @PreAuthorize("hasAnyAuthority('ORGANIZER', 'PLATFORM', 'ADMIN')")
    public List<LocationShortResponse> getRecommendedLocations(int page, int size) {
        return locationService.getRecommendedLocations(page, size);
    }

    @Override
    @PreAuthorize("hasAnyAuthority('PLATFORM', 'ADMIN')")
    public String save(LocationRequest locationRequest) {
        return locationService.save(locationRequest);
    }

    @Override
    @PreAuthorize("(hasAuthority('PLATFORM') and @locationService.isOwner(#id, principal)) or hasAuthority('ADMIN')")
    public void update(String id, LocationRequest locationRequest) {
        locationService.update(id, locationRequest);
    }

    @Override
    @PreAuthorize("(hasAuthority('PLATFORM') and @locationService.isOwner(#id, principal)) or hasAuthority('ADMIN')")
    public void delete(String id) {
        locationService.delete(id);
    }
}
