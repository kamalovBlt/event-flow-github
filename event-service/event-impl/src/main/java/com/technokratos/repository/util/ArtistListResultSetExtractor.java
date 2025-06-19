package com.technokratos.repository.util;

import com.technokratos.model.Artist;
import com.technokratos.model.Event;
import com.technokratos.model.EventCategory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component
public class ArtistListResultSetExtractor implements ResultSetExtractor<List<Artist>> {

    @Override
    public List<Artist> extractData(ResultSet rs) throws SQLException, DataAccessException {
        List<Artist> artists = new ArrayList<>();
        Artist currentArtist = null;
        Long currentArtistId = null;

        while (rs.next()) {
            Long artistId = rs.getLong("id");
            if (!Objects.equals(artistId, currentArtistId)) {
                currentArtist = Artist.builder()
                        .id(artistId)
                        .firstName(rs.getString("first_name"))
                        .lastName(rs.getString("last_name"))
                        .nickname(rs.getString("nickname"))
                        .description(rs.getString("description"))
                        .creatorId(rs.getLong("creator_id"))
                        .events(new ArrayList<>())
                        .build();
                artists.add(currentArtist);
                currentArtistId = artistId;
            }
            Long eventId = rs.getLong("event_id");
            if (rs.wasNull()) {
                continue;
            }
            Event event = Event.builder()
                    .id(eventId)
                    .name(rs.getString("event_name"))
                    .eventCategory(
                            rs.getString("event_category_name") != null
                                    ? EventCategory.valueOf(rs.getString("event_category_name"))
                                    : EventCategory.NO_CATEGORY
                    )                    .build();
            currentArtist.getEvents().add(event);

        }
        return artists;
    }

}

