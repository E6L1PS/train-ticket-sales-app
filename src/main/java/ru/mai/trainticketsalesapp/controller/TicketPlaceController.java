package ru.mai.trainticketsalesapp.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.mai.trainticketsalesapp.model.TicketPlace;
import ru.mai.trainticketsalesapp.service.TicketPlaceService;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("api/v1/ticket")
@RequiredArgsConstructor
public class TicketPlaceController {
    private final TicketPlaceService ticketPlaceService;

    @GetMapping
    public Page<TicketPlace> getAll(
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "5") Integer size
    ) {
        return ticketPlaceService.getAll(PageRequest.of(page, size));
    }

    @GetMapping("/{id}")
    public TicketPlace getTicketById(@PathVariable String id) {
        return ticketPlaceService.getById(id);
    }

    @GetMapping("/train/{id}")
    public List<TicketPlace> getTicketsByTrainId(@PathVariable String id) {
        return ticketPlaceService.getTicketsByTrainId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TicketPlace addTicket(TicketPlace ticketPlace) {
        return ticketPlaceService.createTicket(ticketPlace);
    }

    @PutMapping("/buy/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void buyTicket(@PathVariable String id) {
        ticketPlaceService.buyTicket(id);
    }

    @PutMapping("/pay/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public boolean payTicket(@PathVariable String id, @RequestParam BigDecimal money) {
        return ticketPlaceService.payTicket(id, money);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTicketById(@PathVariable String id) {
        ticketPlaceService.deleteTicket(id);
    }
}
