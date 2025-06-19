package com.technokratos.model;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Image {
    private Long id;
    private Long eventId;
    private String key;
}
