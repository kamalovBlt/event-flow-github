package com.technokratos.model;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Artist {
    private Long id;
    private String firstName;
    private String lastName;
    private String nickname;
    private String description;
    private boolean deleted;
    private List<Event> events;
}
