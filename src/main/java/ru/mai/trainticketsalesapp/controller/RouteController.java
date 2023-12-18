package ru.mai.trainticketsalesapp.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
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
    public Page<Route> getAll(
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "5") Integer size
    ) {
        return routeService.getAll(PageRequest.of(page, size));
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
