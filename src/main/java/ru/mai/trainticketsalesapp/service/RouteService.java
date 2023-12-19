package ru.mai.trainticketsalesapp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.mai.trainticketsalesapp.exception.NotFoundException;
import ru.mai.trainticketsalesapp.model.Route;
import ru.mai.trainticketsalesapp.model.Station;
import ru.mai.trainticketsalesapp.repository.RouteRepository;
import ru.mai.trainticketsalesapp.repository.StationRepository;

@Service
@RequiredArgsConstructor
public class RouteService {
    private final StationRepository stationRepository;
    private final RouteRepository routeRepository;

    public Page<Route> getAll(PageRequest pageRequest) {
        return routeRepository.findAll(pageRequest);
    }

    public Page<Station> getAllStation(PageRequest pageRequest) {
        return stationRepository.findAll(pageRequest);
    }

    public Station createStation(Station stationDto) {
        return stationRepository.insert(Station.builder()
                .name(stationDto.getName())
                .build());
    }

    public Route createRoute(Route route) {
        return routeRepository.save(route);
    }


    @Cacheable(key = "#id", value = "route")
    public Route getById(String id) {
        return routeRepository.findById(id).orElseThrow(() -> new NotFoundException("Route by id:" + id + " not found"));
    }
}
