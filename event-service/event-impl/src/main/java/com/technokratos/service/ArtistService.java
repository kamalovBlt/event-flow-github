package com.technokratos.service;

import com.technokratos.model.Artist;
import com.technokratos.model.Event;
import com.technokratos.repository.interfaces.ArtistRepository;
import com.technokratos.repository.interfaces.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ArtistService {

    private final ArtistRepository artistRepository;
    private final EventRepository eventRepository;

    public Artist findById(Long id) {
        Optional<Artist> artistOpt = artistRepository.findById(id);
        artistOpt.ifPresent(artist -> {
            List<Event> events = eventRepository.findByArtistId(artist.getId());
            artist.setEvents(events);
        });
        return artistOpt.orElseThrow(() -> new IllegalArgumentException("Artist not found"));
    }

    public List<Artist> findAll(int page, int size) {
        List<Artist> artists = artistRepository.findAll(page, size);
        for (Artist artist : artists) {
            List<Event> events = eventRepository.findByArtistId(artist.getId());
            artist.setEvents(events);
        }
        return artists;
    }
}
