package ru.mai.trainticketsalesapp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.mai.trainticketsalesapp.exception.NotFoundException;
import ru.mai.trainticketsalesapp.model.Route;
import ru.mai.trainticketsalesapp.repository.RouteRepository;

@Service
@RequiredArgsConstructor
public class RouteService {
    private final RouteRepository routeRepository;

    public Page<Route> getAll(PageRequest pageRequest) {
        return routeRepository.findAll(pageRequest);
    }

    @Cacheable(key = "#id", value = "route")
    public Route getById(String id) {
        return routeRepository.findById(id).orElseThrow(() -> new NotFoundException("Route by id:" + id + " not found"));
    }

    public Route createRoute(Route route) {
        return routeRepository.save(route);
    }

    public void deleteRoute(String id) {
        routeRepository.deleteById(id);
    }
}
