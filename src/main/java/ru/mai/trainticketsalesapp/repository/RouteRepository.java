package ru.mai.trainticketsalesapp.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import ru.mai.trainticketsalesapp.model.Route;

@Repository
public interface RouteRepository extends MongoRepository<Route, String> {
}
