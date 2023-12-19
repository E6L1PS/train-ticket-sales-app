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

    Optional<TrainElastic> findByTickets_IdEquals(@NonNull String id);

    Page<TrainElastic> findByRoute_Stations_NameEqualsIgnoreCaseAndRoute_Stations_NameEqualsIgnoreCaseAndDepartureDateEqualsAndTickets_IsFreePlaceTrue(
            @NonNull String name,
            @NonNull String name1,
            @NonNull LocalDate departureDate,
            Pageable pageable);
}
