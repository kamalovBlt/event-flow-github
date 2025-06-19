package com.technokratos.service.impl;

import com.technokratos.dto.request.ArtistRequest;
import com.technokratos.dto.response.artist.ArtistResponse;
import com.technokratos.exception.artist.ArtistAlreadyExistsException;
import com.technokratos.exception.artist.ArtistNotFoundException;
import com.technokratos.exception.artist.ArtistSaveException;
import com.technokratos.mapper.api.ArtistMapper;
import com.technokratos.model.Artist;
import com.technokratos.repository.interfaces.ArtistRepository;
import com.technokratos.service.interfaces.ArtistService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class ArtistServiceImpl implements ArtistService {

    private final ArtistRepository artistRepository;
    private final ArtistMapper artistMapper;

    @Override
    public ArtistResponse findById(Long id) {
        Artist artist = artistRepository.findById(id).orElseThrow(
                () -> new ArtistNotFoundException("Артист с id %s не найден".formatted(id)));
        return artistMapper.toResponse(artist);
    }

    @Override
    public List<ArtistResponse> findAll(String keywords, int page, int size) {
        return artistRepository.findAll(keywords, page, size).stream()
                .map(artistMapper::toResponse)
                .toList();
    }

    @Override
    public List<ArtistResponse> findAll(int page, int size) {
        return artistRepository.findAll(page, size).stream()
                .map(artistMapper::toResponse)
                .toList();
    }

    @Override
    public long save(ArtistRequest artistRequest) {
        if (artistRepository.existsByNickname(artistRequest.nickname())){
            throw new ArtistAlreadyExistsException("Артист с таким псевдонимом уже существует");
        }

        return artistRepository.save(artistMapper.toEntity(artistRequest)).orElseThrow(
                () -> new ArtistSaveException("Ошибка при сохранении артиста.")
        );
    }

    @Override
    public void update(Long artistId, ArtistRequest artistRequest) {
        if (!artistRepository.existsById(artistId)) {
            throw new ArtistNotFoundException("Артист для удаления не найден");
        }
        if (artistRepository.existsByNickname(artistRequest.nickname())){
            throw new ArtistAlreadyExistsException("Артист с таким псевдонимом уже существует");
        }
        Artist artist = artistMapper.toEntity(artistRequest);
        artist.setId(artistId);
        artistRepository.update(artist);
    }

    @Override
    public void deleteById(Long id) {
        if (!artistRepository.existsById(id)) {
            throw new ArtistNotFoundException("Артист для удаления не найден");
        }
        artistRepository.deleteById(id);
    }

    @Override
    public boolean isCreator(Long artistId, Authentication authentication) {
        Long creatorId = artistRepository.findCreatorIdByArtistId(artistId).orElseThrow(
                () -> new ArtistNotFoundException("Артист не найден, id: %s".formatted(artistId))
        );
        Jwt jwt = (Jwt) authentication.getPrincipal();
        Long userId = jwt.getClaim("user-id");
        return creatorId.equals(userId);
    }
}
