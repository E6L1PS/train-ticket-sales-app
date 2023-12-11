package ru.mai.trainticketsalesapp.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDate;
import java.util.List;

@Document(indexName = "trains")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TrainElastic {
    @Id
    @Field(type = FieldType.Keyword)
    private String id;

    @Field(type = FieldType.Integer, name = "place_count")
    private Integer placeCount;

    @Field(type = FieldType.Date, name = "departure_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate departureDate;

    @Field(type = FieldType.Nested)
    private Route route;

    @Field(type = FieldType.Nested)
    private List<TicketPlace> tickets;

}
