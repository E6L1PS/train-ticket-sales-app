package ru.mai.trainticketsalesapp.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import ru.mai.trainticketsalesapp.model.TicketPlace;
import ru.mai.trainticketsalesapp.model.Train;

import java.util.List;

@Repository
public interface TicketPlaceRepository extends MongoRepository<TicketPlace, String> {
    @Query("{'train':  ?0}")
    List<TicketPlace> findAllByTrain(Train train);


    @Query("{'id': ?0}")
    void updateIsFreePlace(String ticketId, boolean isFreePlace);
}
