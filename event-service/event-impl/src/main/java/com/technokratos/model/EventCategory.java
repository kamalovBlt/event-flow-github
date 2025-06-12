package com.technokratos.model;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventCategory {
    private Long id;
    private String name;
    private boolean deleted;
}
