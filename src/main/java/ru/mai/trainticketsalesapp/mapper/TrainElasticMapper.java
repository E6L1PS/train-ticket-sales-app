package ru.mai.trainticketsalesapp.mapper;

import org.mapstruct.Mapper;
import ru.mai.trainticketsalesapp.model.Train;
import ru.mai.trainticketsalesapp.model.TrainElastic;

@Mapper(componentModel = "spring")
public interface TrainElasticMapper extends Mappable<TrainElastic, Train> {
}