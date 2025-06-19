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
public class EventListResultSetExtractor implements ResultSetExtractor<List<Event>> {

    @Override
    public List<Event> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<Long, Event> eventMap = new LinkedHashMap<>();
        Map<Long, Set<Long>> eventArtistsMap = new HashMap<>();
        Map<Long, Set<Long>> eventImageIdsMap = new HashMap<>();
        while (rs.next()) {
            long eventId = rs.getLong("id");
            Event event = eventMap.get(eventId);
            if (event == null) {
                event = Event.builder()
                        .id(eventId)
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
                event.setArtists(new ArrayList<>());
                event.setImageIds(new ArrayList<>());
                eventMap.put(eventId, event);
                eventArtistsMap.put(eventId, new HashSet<>());
                eventImageIdsMap.put(eventId, new HashSet<>());
            }

            long artistId = rs.getLong("artist_id");
            if (!rs.wasNull()) {
                Set<Long> artistIds = eventArtistsMap.get(eventId);
                if (artistIds.add(artistId)) {
                    event.getArtists().add(Artist.builder()
                            .id(artistId)
                            .firstName(rs.getString("artist_first_name"))
                            .lastName(rs.getString("artist_last_name"))
                            .nickname(rs.getString("artist_nickname"))
                            .build());
                }
            }

            Long imageKey = rs.getLong("image_id");
            if (!rs.wasNull()) {
                Set<Long> imageKeys = eventImageIdsMap.get(eventId);
                if (imageKeys.add(imageKey)) {
                    event.getImageIds().add(imageKey);
                }
            }
        }
        return List.copyOf(eventMap.values());
    }
}
