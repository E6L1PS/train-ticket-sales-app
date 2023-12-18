package ru.mai.trainticketsalesapp.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import ru.mai.trainticketsalesapp.model.TrainElastic;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

@Repository
public interface TrainSearchRepository extends ElasticsearchRepository<TrainElastic, String> {

    Optional<TrainElastic> findByTickets_IdEquals(@NonNull String id);

    Page<TrainElastic> findByRoute_Stations_NameEqualsIgnoreCaseAndRoute_Stations_NameEqualsIgnoreCaseAndDepartureDateEqualsAndTickets_IsFreePlaceTrue(
            @NonNull String name,
            @NonNull String name1,
            @NonNull LocalDate departureDate,
            Pageable pageable);

    Page<TrainElastic> findByRoute_Stations_NameEqualsIgnoreCaseAndRoute_Stations_NameEqualsIgnoreCaseAndDepartureDateEquals(@NonNull String name, @NonNull String name1, @NonNull LocalDate departureDate, Pageable pageable);

    Page<TrainElastic> findByRoute_Stations_NameInIgnoreCaseAndRoute_Stations_NameInIgnoreCaseAndDepartureDateEquals(
            @NonNull String name,
            @NonNull Collection<String> names,
            @NonNull LocalDate departureDate,
            Pageable pageable
    );

    Page<TrainElastic> findByDepartureDateEqualsAndRoute_Stations_NameIn(@NonNull LocalDate departureDate,
                                                                         @NonNull Collection<String> names,
                                                                         Pageable pageable);

    Page<TrainElastic> findByDepartureDateEquals(@NonNull LocalDate departureDate, PageRequest pageRequest);

    @Query("{\"bool\": {\"must\": [" +
            "{\"nested\": {\"path\": \"route\", \"query\": {\"bool\": {\"must\": [{\"match\": {\"route.stations.name\": \"?0\"}}, {\"range\": {\"departureDate\": {\"gte\": \"?2\", \"lte\": \"?2\"}}}]}}}}," +
            "{\"nested\": {\"path\": \"route\", \"query\": {\"bool\": {\"must\": [{\"match\": {\"route.stations.name\": \"?1\"}}, {\"range\": {\"departureDate\": {\"gte\": \"?2\", \"lte\": \"?2\"}}}]}}}}" +
            "]}}")
    Page<TrainElastic> findAvailableTrains(String departureStation, String destinationStation, LocalDate departureDate, PageRequest pageRequest);

}
