package com.technokratos.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "location")
public class Location {
    @Id
    private String id;
    private Long userId;
    private String name;
    private String description;
    private Address address;
    private Double latitude;
    private Double longitude;
    @DBRef
    private List<Hall> halls;
}
