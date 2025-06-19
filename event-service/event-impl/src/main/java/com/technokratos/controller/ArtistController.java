package com.technokratos.controller;

import com.technokratos.api.ArtistApi;
import com.technokratos.dto.request.ArtistRequest;
import com.technokratos.dto.response.artist.ArtistResponse;
import com.technokratos.service.interfaces.ArtistService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ArtistController implements ArtistApi {

    private final ArtistService artistService;

    @Override
    public List<ArtistResponse> find(String keywords, int page, int size) {
        return artistService.findAll(keywords, page, size);
    }

    @Override
    public ArtistResponse findById(Long artistId) {
        return artistService.findById(artistId);
    }

    @Override
    public List<ArtistResponse> findAll(int page, int size) {
        return artistService.findAll(page, size);
    }

    @Override
    @PreAuthorize("(hasAnyAuthority('ORGANIZER', 'ADMIN'))")
    @Validated
    public Long save(@Valid ArtistRequest artistRequest) {
        return artistService.save(artistRequest);
    }

    @Override
    @PreAuthorize("(isAuthenticated() and hasAuthority('ORGANIZER') and @artistServiceImpl.isCreator(#artistId, authentication)) or hasAuthority('ADMIN')")
    @Validated
    public void update(Long artistId, @Valid ArtistRequest artistRequest) {
        artistService.update(artistId, artistRequest);
    }

    @Override
    @PreAuthorize("(isAuthenticated() and hasAuthority('ORGANIZER') and @artistServiceImpl.isCreator(#artistId, authentication)) or hasAuthority('ADMIN')")
    public void delete(Long artistId) {
        artistService.deleteById(artistId);
    }

}
