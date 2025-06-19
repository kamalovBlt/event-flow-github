package com.technokratos.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "hall")
@Builder
public class Hall {
    @Id
    private String id;
    private String name;
    private List<Row> rows;
}
