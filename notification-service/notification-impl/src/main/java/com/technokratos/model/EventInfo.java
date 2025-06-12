package com.technokratos.model;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventInfo {

    private String name;
    private LocalDateTime time;
    private List<String> artistsNames;
    private String url;

}
