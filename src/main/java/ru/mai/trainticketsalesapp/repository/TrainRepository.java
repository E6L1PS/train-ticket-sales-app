package ru.mai.trainticketsalesapp.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import ru.mai.trainticketsalesapp.model.Train;

@Repository
public interface TrainRepository extends MongoRepository<Train, String> {

    @Query("{'tickets.id': ?0}")
    Train findByTicketId(String ticketId);
}
