package com.technokratos.service.api;

import com.technokratos.dto.request.LocationRequest;
import com.technokratos.dto.response.LocationResponse;
import com.technokratos.dto.response.LocationShortResponse;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface LocationService {

    LocationResponse findById(String id);
    String save(LocationRequest locationRequest);
    List<LocationShortResponse> getRecommendedLocations(int page, int size);
    void update(String id, LocationRequest locationRequest);
    void delete(String id);
    boolean isOwner(String locationId, Authentication principal);

}
