package ru.mai.trainticketsalesapp.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.mai.trainticketsalesapp.model.Route;
import ru.mai.trainticketsalesapp.service.RouteService;

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
    @ResponseStatus(HttpStatus.CREATED)
    public Route addRoute(@RequestBody Route route) {
        return routeService.createRoute(route);
    }


}
