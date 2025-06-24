package com.technokratos.controller;

import com.technokratos.api.LocationApi;
import com.technokratos.dto.request.LocationRequest;
import com.technokratos.dto.response.LocationResponse;
import com.technokratos.dto.response.LocationShortResponse;
import com.technokratos.service.api.LocationService;
import jakarta.validation.Valid;
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
    public LocationResponse findById(String id) {
        return locationService.findById(id);
    }

    @Override
    public List<LocationShortResponse> getRecommendedLocations(int page, int size) {
        return locationService.getRecommendedLocations(page, size);
    }

    @Override
    @PreAuthorize("hasAnyAuthority('PLATFORM', 'ADMIN')")
    @Validated
    public String save(@Valid LocationRequest locationRequest) {
        return locationService.save(locationRequest);
    }

    @Override
    @PreAuthorize("(isAuthenticated() and hasAuthority('PLATFORM') and @locationServiceImpl.isOwner(#id, authentication)) or hasAuthority('ADMIN')")
    @Validated
    public void update(String id, @Valid LocationRequest locationRequest) {
        locationService.update(id, locationRequest);
    }

    @Override
    @PreAuthorize("(isAuthenticated() and hasAuthority('PLATFORM') and @locationServiceImpl.isOwner(#id, authentication)) or hasAuthority('ADMIN')")
    public void delete(String id) {
        locationService.delete(id);
    }
}
