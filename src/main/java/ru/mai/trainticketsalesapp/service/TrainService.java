package ru.mai.trainticketsalesapp.service;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import ru.mai.trainticketsalesapp.dto.TrainDto;
import ru.mai.trainticketsalesapp.model.TicketPlace;
import ru.mai.trainticketsalesapp.model.Train;
import ru.mai.trainticketsalesapp.repository.RouteRepository;
import ru.mai.trainticketsalesapp.repository.TicketPlaceRepository;
import ru.mai.trainticketsalesapp.repository.TrainRepository;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class TrainService {

    private final TrainRepository trainRepository;
    private final RouteRepository routeRepository;
    private final TicketPlaceRepository ticketPlaceRepository;

    public List<Train> getAll() {
        return trainRepository.findAll();
    }

    public Train createTrain(TrainDto trainDto) {
        // Route route = routeRepository.findOne();
        Integer placeCount = trainDto.getPlaceCount();
        List<TicketPlace> tickets = IntStream.range(1, placeCount + 1)
                .mapToObj(i -> TicketPlace.builder()
                        .price(trainDto.getPriceForPlace())
                        .isFreePlace(true)
                        .place(i)
                        .build())
                .collect(Collectors.toList());

        ticketPlaceRepository.insert(tickets);

        return trainRepository.save(Train.builder()
                .route(trainDto.getRoute())
                .placeCount(trainDto.getPlaceCount())
                .departureDate(trainDto.getDepartureDate())
                .tickets(tickets)
                .build());
    }


    public void deleteTrain(ObjectId id) {

    }
}

