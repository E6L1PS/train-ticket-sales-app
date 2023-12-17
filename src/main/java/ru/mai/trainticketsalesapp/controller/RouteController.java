package ru.mai.trainticketsalesapp.controller;

import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.mai.trainticketsalesapp.dto.StationDto;
import ru.mai.trainticketsalesapp.model.Route;
import ru.mai.trainticketsalesapp.model.Station;
import ru.mai.trainticketsalesapp.service.RouteService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/route")
@RequiredArgsConstructor
public class RouteController {
    private final RouteService routeService;

    @GetMapping
    public List<Route> getAll() {
        return routeService.getAll();
    }
    @GetMapping("/{id}")
    public Route getRoute(@PathVariable String id) {
        return routeService.getById(id);
    }

    @PostMapping
    public Route addRoute(@RequestBody List<Station> routes) {
        return routeService.createRoute(routes);
    }


}
