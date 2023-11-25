package ru.mai.trainticketsalesapp.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;


@Document(collection = "tickets")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TicketPlace {
    @Id
    private String id;
    private Integer place;
    private BigDecimal price;
    private Boolean isFreePlace;

}
