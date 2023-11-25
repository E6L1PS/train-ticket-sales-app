package ru.mai.trainticketsalesapp.mapper;

import org.mapstruct.Mapper;
import ru.mai.trainticketsalesapp.dto.TrainDto;
import ru.mai.trainticketsalesapp.model.Train;

@Mapper(componentModel = "spring")
public interface TrainMapper extends Mappable<Train, TrainDto> {
}
