package ru.mai.trainticketsalesapp.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;
import ru.mai.trainticketsalesapp.model.RouteElastic;

@Repository
public interface RouteSearchRepository extends ElasticsearchRepository<RouteElastic, String> {
}
