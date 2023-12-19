package ru.mai.trainticketsalesapp.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.mai.trainticketsalesapp.model.Station;
import ru.mai.trainticketsalesapp.service.RouteService;

@RestController
@RequestMapping("/api/v1/station")
@RequiredArgsConstructor
public class StationController {

    private final RouteService routeService;

    @GetMapping
    public Page<Station> getAllStation(
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "5") Integer size
    ) {
        return routeService.getAllStation(PageRequest.of(page, size));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Station addStation(@RequestBody Station station) {
        return routeService.createStation(station);
    }

}
