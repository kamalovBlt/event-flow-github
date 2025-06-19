package com.technokratos.mapper.impl;

import com.technokratos.dto.request.ArtistRequest;
import com.technokratos.dto.response.artist.ArtistResponse;
import com.technokratos.dto.response.event.EventShortResponse;
import com.technokratos.mapper.api.ArtistMapper;
import com.technokratos.mapper.api.EventCategoryMapper;
import com.technokratos.model.Artist;
import com.technokratos.model.Event;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ArtistMapperImpl implements ArtistMapper {

    private final EventCategoryMapper eventCategoryMapper;

    @Override
    public ArtistResponse toResponse(Artist entity) {
        List<EventShortResponse> events = entity.getEvents() == null ? List.of() :
                entity.getEvents().stream()
                        .map(event -> new EventShortResponse(
                                event.getId(),
                                event.getName(),
                                eventCategoryMapper.toResponse(event.getEventCategory())
                        ))
                        .toList();

        return new ArtistResponse(
                entity.getFirstName(),
                entity.getLastName(),
                entity.getNickname(),
                entity.getDescription(),
                events
        );
    }

    @Override
    public Artist toEntity(ArtistRequest request) {
        List<Event> events = request.eventIds() == null ? List.of() :
                request.eventIds().stream()
                        .map(eventId -> Event.builder()
                                .id(eventId)
                                .build())
                        .toList();

        return Artist.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .nickname(request.nickname())
                .description(request.description())
                .events(events)
                .creatorId(request.userId())
                .build();
    }


}
