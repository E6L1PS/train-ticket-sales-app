package ru.mai.trainticketsalesapp.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "routes")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Route {

    @Id
    private String objectId;

    private String numberRoute;

    private List<Station> stations;

}
