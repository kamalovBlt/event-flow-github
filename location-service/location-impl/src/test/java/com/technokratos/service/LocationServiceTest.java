package com.technokratos.service;

import com.technokratos.config.KafkaMockConfig;
import com.technokratos.dto.AddressDto;
import com.technokratos.dto.request.HallRequest;
import com.technokratos.dto.request.LocationRequest;
import com.technokratos.dto.request.RowRequest;
import com.technokratos.dto.request.SeatRequest;
import com.technokratos.dto.response.LocationResponse;
import com.technokratos.dto.response.LocationShortResponse;
import com.technokratos.exception.LocationNotFoundException;
import com.technokratos.mapper.LocationMapper;
import com.technokratos.model.Hall;
import com.technokratos.model.Location;
import com.technokratos.repository.HallRepository;
import com.technokratos.repository.LocationRepository;
import com.technokratos.service.impl.LocationServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(
        properties = {"mongock.enabled=false"},
        classes = KafkaMockConfig.class
)
@ActiveProfiles("test")
class LocationServiceTest {

    @Autowired
    LocationServiceImpl locationService;

    @Autowired
    private LocationMapper locationMapper;

    @MockitoBean
    LocationRepository locationRepository;

    @MockitoBean
    HallRepository hallRepository;

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
        LocationRequest request = new LocationRequest(
                "New Location",
                1L,
                "New Description",
                25.0, 35.0,
                new AddressDto("123 Main St", "City", "State", "12345"),
                List.of(
                        new HallRequest("Hall 1",
                                List.of(new RowRequest(1, List.of(new SeatRequest(1)))))
                )

        );

        Location returnLocation = locationMapper.toEntity(request);
        returnLocation.setId("12345");

        when(locationRepository.save(any())).thenReturn(returnLocation);
        when(hallRepository.save(any())).thenReturn(Hall.builder().build());

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
        assertEquals("Test Location", locations.get(0).name());
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

    @Test
    void whenUpdateShouldThrowNotFoundException() {
        String testLocationId = "12345";
        LocationRequest request = new LocationRequest(
                "Updated Location",
                1L,
                "Updated Description",
                30.0,
                40.0,
                null,
                null
        );

        when(locationRepository.existsById(testLocationId)).thenReturn(false);

        assertThrows(LocationNotFoundException.class, () -> locationService.update(testLocationId, request));
    }

    @Test
    void shouldReturnTrueIfUserIsOwner() {
        String locationId = "12345";
        Long userId = 42L;

        Location location = new Location(locationId, userId, "Test Location", "Description",
                null, 10.0, 20.0, null);

        when(locationRepository.findUserIdById(locationId)).thenReturn(Optional.of(location));

        Jwt jwt = mock(Jwt.class);
        when(jwt.getClaim("user-id")).thenReturn(userId);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(jwt);

        boolean result = locationService.isOwner(locationId, authentication);
        assertTrue(result);
    }

    @Test
    void shouldReturnFalseIfUserIsNotOwner() {
        String locationId = "12345";
        Long locationOwnerId = 42L;
        Long anotherUserId = 99L;

        Location location = new Location(locationId, locationOwnerId, "Test Location", "Description",
                null, 10.0, 20.0, null);

        when(locationRepository.findUserIdById(locationId)).thenReturn(Optional.of(location));

        Jwt jwt = mock(Jwt.class);
        when(jwt.getClaim("user-id")).thenReturn(anotherUserId);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(jwt);

        boolean result = locationService.isOwner(locationId, authentication);
        assertFalse(result);
    }

    @Test
    void shouldThrowIfLocationNotFound() {
        String locationId = "not-found";
        Long userId = 42L;

        when(locationRepository.findUserIdById(locationId)).thenReturn(Optional.empty());

        Jwt jwt = mock(Jwt.class);
        when(jwt.getClaim("user-id")).thenReturn(userId);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(jwt);

        assertThrows(LocationNotFoundException.class, () -> locationService.isOwner(locationId, authentication));
    }

}