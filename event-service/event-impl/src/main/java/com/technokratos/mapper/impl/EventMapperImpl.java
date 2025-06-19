package com.technokratos.mapper.impl;

import com.technokratos.dto.request.EventRequest;
import com.technokratos.dto.response.artist.ArtistShortResponse;
import com.technokratos.dto.response.event.EventResponse;
import com.technokratos.mapper.api.EventCategoryMapper;
import com.technokratos.mapper.api.EventMapper;
import com.technokratos.model.Artist;
import com.technokratos.model.Event;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class EventMapperImpl implements EventMapper {

    private final EventCategoryMapper eventCategoryMapper;

    @Override
    public EventResponse toResponse(Event entity) {
        List<ArtistShortResponse> artistShortResponses = entity.getArtists() == null ? List.of() :
                entity.getArtists().stream()
                        .map(artist -> new ArtistShortResponse(
                                artist.getId(),
                                artist.getFirstName(),
                                artist.getLastName(),
                                artist.getDescription()
                        ))
                        .toList();
        return new EventResponse(
                entity.getName(),
                entity.getDescription(),
                eventCategoryMapper.toResponse(entity.getEventCategory()),
                entity.getLocationId(),
                entity.getHallId(),
                entity.getStart(),
                entity.getEnd(),
                artistShortResponses,
                entity.getImageIds()
        );
    }

    @Override
    public Event toEntity(EventRequest request) {
        List<Artist> artists = request.artistIds().stream()
                .map(id -> Artist.builder()
                        .id(id)
                        .build())
                .toList();
        return Event.builder()
                .name(request.name())
                .description(request.description())
                .eventCategory(eventCategoryMapper.toEntity(request.category()))
                .locationId(request.locationId())
                .hallId(request.hallId())
                .creatorId(request.userId())
                .start(request.startTime())
                .end(request.endTime())
                .artists(artists)
                .build();
    }

}
