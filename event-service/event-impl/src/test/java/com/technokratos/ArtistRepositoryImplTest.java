package com.technokratos;

import com.technokratos.model.Artist;
import com.technokratos.repository.impl.ArtistRepositoryImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ArtistRepositoryImplTest extends RepositoryTestBase {

    @Autowired
    private ArtistRepositoryImpl artistRepository;

    @Test
    void shouldSaveAndFindById() {
        Artist artist = new Artist(null, "John", "Doe",
                "johndoe", "Test artist", false, null);
        long id = artistRepository.save(artist);

        Optional<Artist> found = artistRepository.findById(id);

        assertTrue(found.isPresent());
        assertEquals("John", found.get().getFirstName());
        assertEquals("Doe", found.get().getLastName());
        assertEquals("johndoe", found.get().getNickname());
        assertEquals("Test artist", found.get().getDescription());
    }

    @Test
    void shouldFindByIdNotFound() {
        Optional<Artist> found = artistRepository.findById(999L);
        assertFalse(found.isPresent());
    }

    @Test
    void shouldFindAll() {
        artistRepository.save(new Artist(1L, "John", "Doe", "johndoe", "Artist 1", false, null));
        artistRepository.save(new Artist(2L, "Jane", "Smith", "janesmith", "Artist 2", false, null));

        List<Artist> artists = artistRepository.findAll(1, 10);
        assertEquals(2, artists.size());
    }

    @Test
    void shouldUpdate() {
        long id = artistRepository.save(new Artist(1L, "John", "Doe",
                "johndoe", "Test artist", false, null));

        artistRepository.update(new Artist(id, "John", "Updated",
                "johndoe", "Test artist", false, null));

        Optional<Artist> updated = artistRepository.findById(id);
        assertTrue(updated.isPresent());
        assertEquals("Updated", updated.get().getLastName());
    }

    @Test
    void shouldDelete() {
        Artist artist = new Artist(1L, "John", "Doe", "johndoe", "Test artist", false, null);
        long id = artistRepository.save(artist);

        artistRepository.deleteById(id);

        Optional<Artist> deleted = artistRepository.findById(id);
        assertFalse(deleted.isPresent());
    }
}