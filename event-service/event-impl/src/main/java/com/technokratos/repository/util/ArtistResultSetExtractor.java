package com.technokratos.repository.util;

import com.technokratos.model.Artist;
import com.technokratos.model.Event;
import com.technokratos.model.EventCategory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
public class ArtistResultSetExtractor implements ResultSetExtractor<Artist> {

    @Override
    public Artist extractData(ResultSet rs) throws SQLException, DataAccessException {
        if (!rs.next()) {
            return null;
        }
        Artist artist = Artist.builder()
                .id(rs.getLong("id"))
                .firstName(rs.getString("first_name"))
                .lastName(rs.getString("last_name"))
                .nickname(rs.getString("nickname"))
                .description(rs.getString("description"))
                .creatorId(rs.getLong("creator_id"))
                .build();
        List<Event> events = new ArrayList<>();
        do {
            long eventId = rs.getLong("event_id");
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
                    )
                    .build();
            events.add(event);
        } while (rs.next());
        artist.setEvents(events);
        return artist;
    }

}
