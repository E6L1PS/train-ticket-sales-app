package ru.mai.trainticketsalesapp.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;
import ru.mai.trainticketsalesapp.dto.TrainDto;
import ru.mai.trainticketsalesapp.model.Route;
import ru.mai.trainticketsalesapp.model.TicketPlace;
import ru.mai.trainticketsalesapp.model.Train;
import ru.mai.trainticketsalesapp.model.TrainElastic;
import ru.mai.trainticketsalesapp.repository.RouteRepository;
import ru.mai.trainticketsalesapp.repository.TicketPlaceRepository;
import ru.mai.trainticketsalesapp.repository.TrainRepository;
import ru.mai.trainticketsalesapp.repository.TrainSearchRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
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

    private final ElasticsearchOperations elasticsearchOperations;


    public List<TrainElastic> search(String departureDate, PageRequest pageRequest) {
        Query searchQuery = NativeQuery.builder()
                .withFilter(q -> q.match(m -> m.field("departureDate").query(departureDate)))
                .withPageable(pageRequest)
                .build();

        SearchHits<TrainElastic> trainHits = elasticsearchOperations.search(searchQuery, TrainElastic.class, IndexCoordinates.of("trains"));

        List<TrainElastic> trainMatches = new ArrayList<>();
        trainHits.forEach(searchHit -> {
            trainMatches.add(searchHit.getContent());
        });

        return trainMatches;
    }

    public Iterable<TrainElastic> findAll() {
        return trainSearchRepository.findAll();
    }

    public Page<TrainElastic> searchTrainsByDate(String departureStation, String destinationStation, String departureDate, PageRequest pageRequest) {
        //findAvailableTrains(departureStation, destinationStation, LocalDate.of(2024, 1, 24), pageRequest);
        //

        return trainSearchRepository.findByDepartureDateEquals(LocalDate.of(2024, 1, 24), pageRequest);
    }


    public Page<TrainElastic> searchTrainsByDateAndStations(String departureStation, String destinationStation, LocalDate departureDate, PageRequest pageRequest) {
        //findAvailableTrains(departureStation, destinationStation, LocalDate.of(2024, 1, 24), pageRequest);
        return trainSearchRepository
                .findByRoute_Stations_NameEqualsIgnoreCaseAndRoute_Stations_NameEqualsIgnoreCaseAndDepartureDateEqualsAndTickets_IsFreePlaceTrue(
                        departureStation,
                        destinationStation,
                        departureDate,
                        pageRequest
                );
    }

    public Train createTrain(TrainDto trainDto) {
        Integer placeCount = trainDto.getPlaceCount();
        List<TicketPlace> tickets = ticketPlaceRepository.insert(IntStream.range(1, placeCount + 1)
                .mapToObj(i -> TicketPlace.builder()
                        .price(trainDto.getPriceForPlace())
                        .isFreePlace(true)
                        .place(i)
                        .build())
                .collect(Collectors.toList()));

        Route route = routeRepository.findById(trainDto.getRoute().getId())
                .orElseGet(() -> routeRepository.insert(trainDto.getRoute()));

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
                .route(train.getRoute())
                .tickets(tickets)
                .build();

        log.info(trainElastic.getId());

        trainSearchRepository.save(trainElastic);

        return train;
    }


    public void deleteTrain(ObjectId id) {

    }

}

