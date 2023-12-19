package ru.mai.trainticketsalesapp.dto;

import lombok.*;
import ru.mai.trainticketsalesapp.model.Route;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TrainDto {
    private LocalDate departureDate;
    private Route route;
    private Integer placeCount;
    private BigDecimal priceForPlace;
}
