package ru.mai.trainticketsalesapp.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.List;


@Document(collection = "trains")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Train {
    @Id
    private String id;
    private Integer placeCount;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate departureDate;
    private Route route;

    private List<TicketPlace> tickets;

}
