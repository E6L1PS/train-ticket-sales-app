package ru.mai.trainticketsalesapp.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import ru.mai.trainticketsalesapp.model.TicketPlace;

@Repository
public interface TicketPlaceRepository extends MongoRepository<TicketPlace, String> {
}
