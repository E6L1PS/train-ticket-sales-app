package ru.mai.trainticketsalesapp.service;

import com.github.javafaker.Faker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.mai.trainticketsalesapp.dto.TrainDto;
import ru.mai.trainticketsalesapp.exception.NotFoundException;
import ru.mai.trainticketsalesapp.model.*;
import ru.mai.trainticketsalesapp.repository.RouteRepository;
import ru.mai.trainticketsalesapp.repository.TicketPlaceRepository;
import ru.mai.trainticketsalesapp.repository.TrainRepository;
import ru.mai.trainticketsalesapp.repository.TrainSearchRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrainService {

    private final TrainRepository trainRepository;
    private final RouteRepository routeRepository;
    private final TicketPlaceRepository ticketPlaceRepository;
    private final TrainSearchRepository trainSearchRepository;

    public Page<TrainElastic> findAll(Pageable pageable) {
        return trainSearchRepository.findAll(pageable);
    }

    public TrainElastic findById(String id) {
        return trainSearchRepository.findById(id).orElseThrow(() -> new NotFoundException("Train " + id + " not found."));
    }

    public Page<TrainElastic> search(Boolean isFreePlace, String departureStation, String destinationStation, LocalDate departureDate, PageRequest pageRequest) {
        return trainSearchRepository
                .findByTickets_IsFreePlaceEqualsAndDepartureDateEqualsAndRoute_Stations_NameEqualsIgnoreCaseAndRoute_Stations_NameEqualsIgnoreCase(
                        isFreePlace,
                        departureDate,
                        departureStation,
                        destinationStation,
                        pageRequest
                );
    }

    public void generateAndSaveTrains(Long NUMBER_OF_TRAINS_TO_GENERATE) {
        for (int i = 0; i < NUMBER_OF_TRAINS_TO_GENERATE; i++) {
            TrainDto trainDto = generateRandomTrainDto();
            createTrain(trainDto);
        }
    }

    private TrainDto generateRandomTrainDto() {
        Faker faker = new Faker();
        return TrainDto.builder()
                .placeCount(faker.number().numberBetween(10, 60))
                .priceForPlace(BigDecimal.valueOf(faker.number().randomDouble(2, 1000, 20000)))
                .route(generateRandomRoute())
                .departureDate(faker.date().future(30, TimeUnit.DAYS).toInstant().atZone(ZoneId.systemDefault()).toLocalDate())
                .build();
    }

    private Route generateRandomRoute() {
        Faker faker = new Faker();

        List<Station> stations = new ArrayList<>();
        for (int j = 0; j < faker.random().nextInt(10); j++) {
            stations.add(Station.builder()
                    .name(faker.address().city())
                    .build());
        }

        return Route.builder()
                .numberRoute("#" + faker.number().numberBetween(1, 500))
                .stations(stations)
                .build();
    }

    public TrainElastic createTrain(TrainDto trainDto) {
        Integer placeCount = trainDto.getPlaceCount();
        List<TicketPlace> tickets = ticketPlaceRepository.insert(IntStream.range(1, placeCount + 1)
                .mapToObj(i -> TicketPlace.builder()
                        .price(trainDto.getPriceForPlace())
                        .isFreePlace(true)
                        .place(i)
                        .build())
                .collect(Collectors.toList()));

        log.info("Route.getNumberRoute: {}", trainDto.getRoute().getNumberRoute());

        Route route = routeRepository.insert(trainDto.getRoute());

        log.info("RouteId: {}", route.getId());
        Train train = trainRepository.save(Train.builder()
                .route(route)
                .placeCount(placeCount)
                .departureDate(trainDto.getDepartureDate())
                .tickets(tickets)
                .build());

        TrainElastic trainElastic = TrainElastic.builder()
                .id(train.getId())
                .departureDate(train.getDepartureDate())
                .placeCount(train.getPlaceCount())
                .route(route)
                .tickets(tickets)
                .build();

        log.info(trainElastic.getId());


        return trainSearchRepository.save(trainElastic);
    }

    public void deleteTrain(String id) {
        trainRepository.deleteById(id);
        trainSearchRepository.deleteById(id);
    }

    public void deleteAll() {
        ticketPlaceRepository.deleteAll();
        routeRepository.deleteAll();
        trainRepository.deleteAll();
        trainSearchRepository.deleteAll();
    }

    public TrainElastic updateTrain(Train train) {
        trainRepository.save(train);
        TrainElastic trainElastic = TrainElastic.builder()
                .id(train.getId())
                .departureDate(train.getDepartureDate())
                .placeCount(train.getPlaceCount())
                .route(train.getRoute())
                .tickets(train.getTickets())
                .build();
        return trainSearchRepository.save(trainElastic);
    }
}