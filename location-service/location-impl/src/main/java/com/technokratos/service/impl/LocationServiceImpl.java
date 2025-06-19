package com.technokratos.service.impl;

import com.technokratos.dto.kafkaMessage.UpdateSeatMessage;
import com.technokratos.dto.request.LocationRequest;
import com.technokratos.dto.response.LocationResponse;
import com.technokratos.dto.response.LocationShortResponse;
import com.technokratos.exception.LocationNotFoundException;
import com.technokratos.kafka.LocationEventProducer;
import com.technokratos.mapper.LocationMapper;
import com.technokratos.model.Hall;
import com.technokratos.model.Location;
import com.technokratos.repository.HallRepository;
import com.technokratos.repository.LocationRepository;
import com.technokratos.service.api.LocationService;
import com.technokratos.service.util.SeatDiffUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService {

    private final HallRepository hallRepository;
    private final LocationRepository locationRepository;

    private final LocationMapper locationMapper;

    private final LocationEventProducer locationEventProducer;

    @Override
    public LocationResponse findById(String id) {
        return locationMapper.toResponse(
                locationRepository.findById(id).orElseThrow(() -> new LocationNotFoundException(id)));
    }

    @Override
    public String save(LocationRequest locationRequest) {
        Location location = locationMapper.toEntity(locationRequest);

        List<Hall> savedHalls = location.getHalls().stream()
                .map(hallRepository::save)
                .toList();

        location.setHalls(savedHalls);

        return locationRepository.save(location).getId();
    }

    @Override
    public List<LocationShortResponse> getRecommendedLocations(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return locationRepository.findAll(pageable).stream()
                .map(locationMapper::toShortResponse)
                .toList();
    }

    @Override
    @Transactional
    public void update(String id, LocationRequest newRequest) {
        Location oldLocation = locationRepository.findById(id)
                .orElseThrow(() -> new LocationNotFoundException(id));

        Location updatedLocation = locationMapper.toEntity(newRequest);
        updatedLocation.setId(oldLocation.getId());

        List<Hall> halls = updatedLocation.getHalls();

        List<Hall> savedHalls = halls.stream()
                .map(hallRepository::save)
                .toList();
        updatedLocation.setHalls(savedHalls);

        locationRepository.save(updatedLocation);

        List<UpdateSeatMessage> updateSeats = SeatDiffUtil.compareLocations(oldLocation, updatedLocation);

        if (!updateSeats.isEmpty()) {
            locationEventProducer.sendUpdateSeat(updateSeats);
        }
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
