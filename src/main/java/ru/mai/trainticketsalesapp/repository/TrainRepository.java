package ru.mai.trainticketsalesapp.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import ru.mai.trainticketsalesapp.model.Train;

@Repository
public interface TrainRepository extends MongoRepository<Train, String> {
}
