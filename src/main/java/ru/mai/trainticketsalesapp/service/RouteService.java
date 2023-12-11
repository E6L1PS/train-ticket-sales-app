package ru.mai.trainticketsalesapp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.mai.trainticketsalesapp.model.Route;
import ru.mai.trainticketsalesapp.model.Station;
import ru.mai.trainticketsalesapp.repository.RouteRepository;
import ru.mai.trainticketsalesapp.repository.TrainSearchRepository;
import ru.mai.trainticketsalesapp.repository.StationRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RouteService {
    private final StationRepository stationRepository;
    private final RouteRepository routeRepository;

    public List<Route> getAll() {
        return routeRepository.findAll();
    }

    public List<Station> getAllStation() {
        return stationRepository.findAll();
    }

    public Station createStation(Station stationDto) {
        return stationRepository.insert(Station.builder()
                .name(stationDto.getName())
                .build());
    }

    public Route createRoute(List<Station> stations) {
        return routeRepository.save(Route.builder().numberRoute("#").stations(stations).build());
    }


}
