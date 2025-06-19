package com.technokratos.model;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

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
    private String hallId;
    private String description;
    private LocalDateTime start;
    private LocalDateTime end;
    private Boolean canceled;
    private String videoKey;
    private Integer popularity;
    private boolean deleted;
    private Long creatorId;
    private List<Long> imageIds;
    private List<Artist> artists;

}
