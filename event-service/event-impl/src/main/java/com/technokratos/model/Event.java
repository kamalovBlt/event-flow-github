package com.technokratos.model;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Event {
    private Long id;
    private String name;
    private EventCategory eventCategory;
    private String locationId;
    private Long hallId;
    private String description;
    private LocalDateTime date;
    private Boolean canceled;
    private String videoKey;
    private Integer popularity;
    private boolean deleted;

    private Set<String> imageKeys;

    private List<Artist> artists;

    private List<Long> organizerIds;
}
