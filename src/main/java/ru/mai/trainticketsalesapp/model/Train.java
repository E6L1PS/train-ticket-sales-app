package ru.mai.trainticketsalesapp.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.List;


@Document(collection = "trains")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Train {
    @Id
    private String id;

    private Integer placeCount;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate departureDate;

    @DBRef(lazy = true)
    private Route route;

    @DBRef(lazy = true)
    private List<TicketPlace> tickets;

}
