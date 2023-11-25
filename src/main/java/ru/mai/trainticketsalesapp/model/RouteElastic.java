package ru.mai.trainticketsalesapp.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.List;

@Document(indexName = "routes")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RouteElastic {

    @Id
    private String objectId;

    private String numberRoute;

    private List<Station> stations;

}
