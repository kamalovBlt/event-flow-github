package com.technokratos.service;

import com.technokratos.dto.request.LocationRequest;
import com.technokratos.dto.response.LocationResponse;
import com.technokratos.dto.response.LocationShortResponse;
import com.technokratos.exception.LocationNotFoundException;
import com.technokratos.mapper.LocationMapper;
import com.technokratos.model.Location;
import com.technokratos.repository.LocationRepository;
import com.technokratos.service.impl.LocationServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(properties = {"mongock.enabled=false"})
@ActiveProfiles("test")
class LocationServiceTest {

    @Autowired
    LocationServiceImpl locationService;

    @Autowired
    private LocationMapper locationMapper;

    @MockitoBean
    LocationRepository locationRepository;

    @Test
    void shouldFindById() {
        String testLocationId = "12345";
        Location location = new Location(testLocationId, 1L, "Test Location", "Description",
                null, 10.0, 20.0, null);
        when(locationRepository.findById(anyString())).thenReturn(Optional.of(location));
        LocationResponse response = locationService.findById(testLocationId);
        assertNotNull(response);
        assertEquals("Test Location", response.name());

        verify(locationRepository).findById(testLocationId);
    }

    @Test
    void shouldFindByIdNotFound() {
        when(locationRepository.findById(anyString())).thenReturn(Optional.empty());
        assertThrows(LocationNotFoundException.class, () -> locationService.findById("999"));
    }

    @Test
    void shouldSave() {
        LocationRequest request = new LocationRequest("New Location", 1L,"New Description",
                15.0, 25.0, null,null);

        Location returnLocation = locationMapper.toEntity(request);
        returnLocation.setId("12345");

        when(locationRepository.save(any())).thenReturn(returnLocation);

        assertEquals("12345", locationService.save(request));
    }

    @Test
    void shouldGetRecommendedLocations() {

        String testLocationId = "12345";
        Location location = new Location(testLocationId, 1L, "Test Location",  "Description",
                null, 10.0, 20.0, null);
        when(locationRepository.findAll((Pageable) any())).thenReturn(new PageImpl<>(List.of(location)));

        List<LocationShortResponse> locations = locationService.getRecommendedLocations(0, 10);
        assertEquals(1, locations.size());
        assertEquals("Test Location", locations.getFirst().name());
    }


    @Test
    void shouldUpdate() {
        String testLocationId = "12345";
        Location existingLocation = new Location(testLocationId, 1L, "Test Location", "Description",
                null, 10.0, 20.0, null);
        when(locationRepository.findById(testLocationId)).thenReturn(Optional.of(existingLocation));

        LocationRequest request = new LocationRequest(
                "Updated Location",
                1L,
                "Updated Description",
                30.0,
                40.0,
                null,
                null
        );

        when(locationRepository.existsById(testLocationId)).thenReturn(true);
        locationService.update(testLocationId, request);

        ArgumentCaptor<Location> captor = ArgumentCaptor.forClass(Location.class);
        verify(locationRepository).save(captor.capture());

        Location saved = captor.getValue();
        assertEquals("Updated Location", saved.getName());
        assertEquals("Updated Description", saved.getDescription());
        assertEquals(30.0, saved.getLatitude());
        assertEquals(40.0, saved.getLongitude());
        assertEquals(testLocationId, saved.getId());
    }

    @Test
    void shouldDelete() {
        String testLocationId = "12345";
        Location location = new Location(testLocationId, 1L, "Test Location", "Description",
                null, 10.0, 20.0, null);
        when(locationRepository.findById(testLocationId)).thenReturn(Optional.of(location));

        locationService.delete(testLocationId);

        verify(locationRepository).delete(location);
    }
}