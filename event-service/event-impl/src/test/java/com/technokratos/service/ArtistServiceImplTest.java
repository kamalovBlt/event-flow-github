package com.technokratos.service;

import com.technokratos.dto.EventCategoryDTO;
import com.technokratos.dto.request.ArtistRequest;
import com.technokratos.dto.response.artist.ArtistResponse;
import com.technokratos.dto.response.event.EventShortResponse;
import com.technokratos.exception.artist.ArtistAlreadyExistsException;
import com.technokratos.exception.artist.ArtistNotFoundException;
import com.technokratos.exception.artist.ArtistSaveException;
import com.technokratos.mapper.api.ArtistMapper;
import com.technokratos.model.Artist;
import com.technokratos.repository.interfaces.ArtistRepository;
import com.technokratos.service.impl.ArtistServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ArtistServiceImplTest {

    @Mock
    private ArtistRepository artistRepository;

    @Mock
    private ArtistMapper artistMapper;

    @InjectMocks
    private ArtistServiceImpl artistService;

    private Artist artist;
    private ArtistResponse artistResponse;
    private ArtistRequest artistRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        artist = new Artist();
        artist.setId(1L);
        artist.setNickname("DJ Test");

        artistResponse = new ArtistResponse(
                "First", "Last", "DJ Test", "Desc",
                List.of(new EventShortResponse(1L, "lala", EventCategoryDTO.SPORT))
        );

        artistRequest = new ArtistRequest(
                "First", "Last", "DJ Test", "Desc", 1L, List.of()
        );
    }

    @Test
    void shouldFindArtistById() {
        when(artistRepository.findById(1L)).thenReturn(Optional.of(artist));
        when(artistMapper.toResponse(artist)).thenReturn(artistResponse);

        ArtistResponse result = artistService.findById(1L);

        assertThat(result).isEqualTo(artistResponse);
        verify(artistRepository).findById(1L);
        verify(artistMapper).toResponse(artist);
    }

    @Test
    void shouldThrowWhenArtistNotFoundById() {
        when(artistRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> artistService.findById(1L))
                .isInstanceOf(ArtistNotFoundException.class)
                .hasMessageContaining("Артист с id 1 не найден");

        verify(artistRepository).findById(1L);
        verifyNoInteractions(artistMapper);
    }

    @Test
    void shouldFindAllWithKeywords() {
        List<Artist> artists = List.of(artist);
        when(artistRepository.findAll("dj", 0, 10)).thenReturn(artists);
        when(artistMapper.toResponse(artist)).thenReturn(artistResponse);

        List<ArtistResponse> result = artistService.findAll("dj", 0, 10);

        assertThat(result).containsExactly(artistResponse);
        verify(artistRepository).findAll("dj", 0, 10);
        verify(artistMapper).toResponse(artist);
    }

    @Test
    void shouldFindAllWithoutKeywords() {
        List<Artist> artists = List.of(artist);
        when(artistRepository.findAll(0, 10)).thenReturn(artists);
        when(artistMapper.toResponse(artist)).thenReturn(artistResponse);

        List<ArtistResponse> result = artistService.findAll(0, 10);

        assertThat(result).containsExactly(artistResponse);
        verify(artistRepository).findAll(0, 10);
        verify(artistMapper).toResponse(artist);
    }

    @Test
    void shouldSaveArtistSuccessfully() {
        when(artistRepository.existsByNickname("DJ Test")).thenReturn(false);
        when(artistMapper.toEntity(artistRequest)).thenReturn(artist);
        when(artistRepository.save(artist)).thenReturn(Optional.of(1L));

        long id = artistService.save(artistRequest);

        assertThat(id).isEqualTo(1L);
        verify(artistRepository).existsByNickname("DJ Test");
        verify(artistMapper).toEntity(artistRequest);
        verify(artistRepository).save(artist);
    }

    @Test
    void shouldThrowWhenSavingArtistWithDuplicateNickname() {
        when(artistRepository.existsByNickname("DJ Test")).thenReturn(true);

        assertThatThrownBy(() -> artistService.save(artistRequest))
                .isInstanceOf(ArtistAlreadyExistsException.class)
                .hasMessageContaining("Артист с таким псевдонимом уже существует");

        verify(artistRepository).existsByNickname("DJ Test");
        verify(artistMapper, never()).toEntity(any());
        verify(artistRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenSaveFails() {
        when(artistRepository.existsByNickname("DJ Test")).thenReturn(false);
        when(artistMapper.toEntity(artistRequest)).thenReturn(artist);
        when(artistRepository.save(artist)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> artistService.save(artistRequest))
                .isInstanceOf(ArtistSaveException.class)
                .hasMessageContaining("Ошибка при сохранении артиста.");

        verify(artistRepository).existsByNickname("DJ Test");
        verify(artistMapper).toEntity(artistRequest);
        verify(artistRepository).save(artist);
    }

    @Test
    void shouldUpdateArtistSuccessfully() {
        when(artistRepository.existsById(1L)).thenReturn(true);
        when(artistRepository.existsByNickname("DJ Test")).thenReturn(false);
        when(artistMapper.toEntity(artistRequest)).thenReturn(artist);

        artistService.update(1L, artistRequest);

        verify(artistRepository).existsById(1L);
        verify(artistRepository).existsByNickname("DJ Test");
        verify(artistMapper).toEntity(artistRequest);
        verify(artistRepository).update(artist);
        assertThat(artist.getId()).isEqualTo(1L);
    }

    @Test
    void shouldThrowWhenUpdatingNonexistentArtist() {
        when(artistRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> artistService.update(1L, artistRequest))
                .isInstanceOf(ArtistNotFoundException.class)
                .hasMessageContaining("Артист для удаления не найден");

        verify(artistRepository).existsById(1L);
        verify(artistRepository, never()).existsByNickname(any());
        verify(artistMapper, never()).toEntity(any());
        verify(artistRepository, never()).update(any());
    }

    @Test
    void shouldThrowWhenUpdatingWithDuplicateNickname() {
        when(artistRepository.existsById(1L)).thenReturn(true);
        when(artistRepository.existsByNickname("DJ Test")).thenReturn(true);

        assertThatThrownBy(() -> artistService.update(1L, artistRequest))
                .isInstanceOf(ArtistAlreadyExistsException.class)
                .hasMessageContaining("Артист с таким псевдонимом уже существует");

        verify(artistRepository).existsById(1L);
        verify(artistRepository).existsByNickname("DJ Test");
        verify(artistMapper, never()).toEntity(any());
        verify(artistRepository, never()).update(any());
    }

    @Test
    void shouldDeleteArtistSuccessfully() {
        when(artistRepository.existsById(1L)).thenReturn(true);

        artistService.deleteById(1L);

        verify(artistRepository).existsById(1L);
        verify(artistRepository).deleteById(1L);
    }

    @Test
    void shouldThrowWhenDeletingNonexistentArtist() {
        when(artistRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> artistService.deleteById(1L))
                .isInstanceOf(ArtistNotFoundException.class)
                .hasMessageContaining("Артист для удаления не найден");

        verify(artistRepository).existsById(1L);
        verify(artistRepository, never()).deleteById(any());
    }

    @Test
    void shouldReturnTrueWhenIsCreator() {
        Authentication authentication = mock(Authentication.class);
        Jwt jwt = mock(Jwt.class);

        when(artistRepository.findCreatorIdByArtistId(1L)).thenReturn(Optional.of(1L));
        when(authentication.getPrincipal()).thenReturn(jwt);
        when(jwt.getClaim("user-id")).thenReturn(1L);

        boolean result = artistService.isCreator(1L, authentication);

        assertThat(result).isTrue();
        verify(artistRepository).findCreatorIdByArtistId(1L);
        verify(authentication).getPrincipal();
        verify(jwt).getClaim("user-id");
    }

    @Test
    void shouldThrowWhenIsCreatorArtistNotFound() {
        Authentication authentication = mock(Authentication.class);

        when(artistRepository.findCreatorIdByArtistId(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> artistService.isCreator(1L, authentication))
                .isInstanceOf(ArtistNotFoundException.class)
                .hasMessageContaining("Артист не найден, id: 1");

        verify(artistRepository).findCreatorIdByArtistId(1L);
        verify(authentication, never()).getPrincipal();
    }
}
