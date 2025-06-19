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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class EventResultSetExtractor implements ResultSetExtractor<Event> {

    @Override
    public Event extractData(ResultSet rs) throws SQLException, DataAccessException {
        if (!rs.next()) {
            return null;
        }
        Event event = Event.builder()
                .id(rs.getLong("id"))
                .name(rs.getString("name"))
                .eventCategory(rs.getString("event_category_name") != null
                        ? EventCategory.valueOf(rs.getString("event_category_name"))
                        : EventCategory.NO_CATEGORY)
                .locationId(rs.getString("location_id"))
                .hallId(rs.getString("hall_id"))
                .description(rs.getString("description"))
                .start(rs.getTimestamp("start_time").toLocalDateTime())
                .end(rs.getTimestamp("end_time").toLocalDateTime())
                .canceled(rs.getBoolean("canceled"))
                .videoKey(rs.getString("video_key"))
                .popularity(rs.getInt("popularity"))
                .deleted(rs.getBoolean("deleted"))
                .creatorId(rs.getLong("creator_id"))
                .build();

        Set<Long> artistIds = new HashSet<>();
        Set<Long> imageIdsSet = new HashSet<>();
        List<Artist> artists = new ArrayList<>();

        do {
            long artistId = rs.getLong("artist_id");
            if (!rs.wasNull() && artistIds.add(artistId)) {
                Artist artist = Artist.builder()
                        .id(artistId)
                        .firstName(rs.getString("artist_first_name"))
                        .lastName(rs.getString("artist_last_name"))
                        .nickname(rs.getString("artist_nickname"))
                        .build();
                artists.add(artist);
            }
            Long imageKey = rs.getLong("image_id");
            if (!rs.wasNull()) {
                imageIdsSet.add(imageKey);
            }
        } while (rs.next());

        event.setArtists(artists);
        event.setImageIds(imageIdsSet.stream().toList());
        return event;
    }
}

