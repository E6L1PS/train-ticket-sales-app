package ru.mai.trainticketsalesapp.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import com.github.javafaker.Faker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;
import ru.mai.trainticketsalesapp.dto.TrainDto;
import ru.mai.trainticketsalesapp.exception.NotFoundException;
import ru.mai.trainticketsalesapp.model.*;
import ru.mai.trainticketsalesapp.repository.RouteRepository;
import ru.mai.trainticketsalesapp.repository.TicketPlaceRepository;
import ru.mai.trainticketsalesapp.repository.TrainRepository;
import ru.mai.trainticketsalesapp.repository.TrainSearchRepository;
import ru.mai.trainticketsalesapp.util.ElasticSearchUtil;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
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
    private final ElasticsearchClient elasticsearchClient;

    public Page<TrainElastic> findAll(Pageable pageable) {
        return trainSearchRepository.findAll(pageable);
    }

    public TrainElastic findById(String id) {
        return trainSearchRepository.findById(id).orElseThrow(() -> new NotFoundException("Train " + id + " not found."));
    }

    public SearchResponse<TrainElastic> fuzzySearch(String approximateDepartureStation) throws IOException {
        Supplier<co.elastic.clients.elasticsearch._types.query_dsl.Query> supplier =
                ElasticSearchUtil.createSupplierQuery(approximateDepartureStation);
        SearchResponse<TrainElastic> response = elasticsearchClient
                .search(s -> s.index("trains").query(supplier.get()), TrainElastic.class);
        log.info("elasticsearch supplier fuzzy query " + supplier.get().toString());
        return response;
    }

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

    public Page<TrainElastic> searchTrainsByDateAndStations(Boolean isFreePlace, String departureStation, String destinationStation, LocalDate departureDate, PageRequest pageRequest) {
        return trainSearchRepository
                .findByTickets_IsFreePlaceEqualsAndDepartureDateEqualsAndRoute_Stations_NameEqualsIgnoreCaseAndRoute_Stations_NameEqualsIgnoreCase (
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