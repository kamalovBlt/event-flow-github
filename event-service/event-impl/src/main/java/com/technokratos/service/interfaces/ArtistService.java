package com.technokratos.service.interfaces;

import com.technokratos.dto.request.ArtistRequest;
import com.technokratos.dto.response.artist.ArtistResponse;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface ArtistService {
    ArtistResponse findById(Long id);
    List<ArtistResponse> findAll(String keywords, int page, int size);
    List<ArtistResponse> findAll(int page, int size);
    long save(ArtistRequest artistRequest);
    void update(Long artistId, ArtistRequest artistRequest);
    void deleteById(Long id);
    boolean isCreator(Long artistId, Authentication authentication);
}
