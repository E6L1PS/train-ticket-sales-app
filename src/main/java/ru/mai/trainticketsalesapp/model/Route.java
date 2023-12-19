package ru.mai.trainticketsalesapp.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.List;

@Document(collection = "routes")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Route implements Serializable {

    @Id
    private String id;

    @Field(type = FieldType.Text, name = "number_route")
    private String numberRoute;

    @Field(type = FieldType.Nested)
    private List<Station> stations;

}
