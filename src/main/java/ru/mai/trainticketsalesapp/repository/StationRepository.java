package ru.mai.trainticketsalesapp.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import ru.mai.trainticketsalesapp.model.Station;

@Repository
public interface StationRepository extends MongoRepository<Station, String> {
}
