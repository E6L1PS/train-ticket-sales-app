package ru.mai.trainticketsalesapp.controller;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.mai.trainticketsalesapp.dto.StationDto;
import ru.mai.trainticketsalesapp.model.Station;
import ru.mai.trainticketsalesapp.service.RouteService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/station")
@RequiredArgsConstructor
public class StationController {

    private final RouteService routeService;
    @GetMapping
    public List<Station> getAllStation() {
        return routeService.getAllStation();
    }

    @PostMapping
    public Station addStation(@RequestBody Station stationDto) {
        return routeService.createStation(stationDto);
    }

}
