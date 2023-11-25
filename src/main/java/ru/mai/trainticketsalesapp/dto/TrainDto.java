package ru.mai.trainticketsalesapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.mai.trainticketsalesapp.model.Route;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TrainDto {
    private LocalDate departureDate;
    private Route route;
    private Integer placeCount;
    private BigDecimal priceForPlace;
}
