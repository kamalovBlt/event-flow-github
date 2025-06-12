package com.technokratos.service.impl;

import com.technokratos.dto.request.LocationRequest;
import com.technokratos.dto.response.LocationResponse;
import com.technokratos.dto.response.LocationShortResponse;
import com.technokratos.exception.LocationNotFoundException;
import com.technokratos.mapper.LocationMapper;
import com.technokratos.model.Location;
import com.technokratos.repository.LocationRepository;
import com.technokratos.service.api.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService {
    private final LocationRepository locationRepository;

    private final LocationMapper locationMapper;

    @Override
    public LocationResponse findById(String id) {
        return locationMapper.toResponse(
                locationRepository.findById(id).orElseThrow(() -> new LocationNotFoundException(id)));
    }

    @Override
    public String save(LocationRequest locationRequest) {
        return locationRepository.save(locationMapper.toEntity(locationRequest)).getId();
    }

    @Override
    public List<LocationShortResponse> getRecommendedLocations(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return locationRepository.findAll(pageable).stream()
                .map(locationMapper::toShortResponse)
                .toList();
    }

    @Override
    public void update(String id, LocationRequest locationRequest) {
        if (!locationRepository.existsById(id)) {
            throw new LocationNotFoundException(id);
        }
        Location location = locationMapper.toEntity(locationRequest);
        location.setId(id);
        locationRepository.save(location);
    }

    @Override
    public void delete(String id) {
        Location location = locationRepository.findById(id).orElseThrow(() -> new LocationNotFoundException(id));
        locationRepository.delete(location);
    }

    @Override
    public boolean isOwner(String locationId, Authentication authentication) {
        Location location = locationRepository.findUserIdById(locationId)
                .orElseThrow(() -> new LocationNotFoundException(locationId));

        Jwt principal = (Jwt) authentication.getPrincipal();
        Long userId = principal.getClaim("user-id");
        return location.getUserId().equals(userId);
    }

}
