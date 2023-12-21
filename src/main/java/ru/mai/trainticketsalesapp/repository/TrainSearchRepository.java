package ru.mai.trainticketsalesapp.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import ru.mai.trainticketsalesapp.model.TrainElastic;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface TrainSearchRepository extends ElasticsearchRepository<TrainElastic, String> {
    Page<TrainElastic> findByTickets_IsFreePlaceEqualsAndDepartureDateEqualsAndRoute_Stations_NameEqualsIgnoreCaseAndRoute_Stations_NameEqualsIgnoreCase(
            @NonNull Boolean isFreePlace,
            @NonNull LocalDate departureDate,
            @NonNull String name,
            @NonNull String name1,
            Pageable pageable);

    Optional<TrainElastic> findByTickets_IdEquals(@NonNull String id);
}
