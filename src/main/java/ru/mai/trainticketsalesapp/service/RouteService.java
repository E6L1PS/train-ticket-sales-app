package ru.mai.trainticketsalesapp.service;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.mai.trainticketsalesapp.model.Route;
import ru.mai.trainticketsalesapp.model.RouteElastic;
import ru.mai.trainticketsalesapp.model.Station;
import ru.mai.trainticketsalesapp.repository.RouteRepository;
import ru.mai.trainticketsalesapp.repository.RouteSearchRepository;
import ru.mai.trainticketsalesapp.repository.StationRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RouteService {
    private final StationRepository stationRepository;
    private final RouteRepository routeRepository;

    private final RouteSearchRepository routeSearchRepository;

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
        Route route = routeRepository.save(Route.builder().numberRoute("#").stations(stations).build());

        routeSearchRepository.save(RouteElastic.builder()
                .objectId(route.getObjectId())
                .numberRoute(route.getNumberRoute())
                .stations(route.getStations())
                .build());

        return route;
    }


}
