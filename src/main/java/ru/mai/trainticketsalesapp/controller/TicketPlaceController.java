package ru.mai.trainticketsalesapp.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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

    @GetMapping("/train/{id}")
    public List<TicketPlace> getTicketsByTrainId(@PathVariable String id) {
        return ticketPlaceService.getTicketsByTrainId(id);
    }

    @GetMapping("/{id}")
    public TicketPlace getTicketById(@PathVariable String id) {
        return ticketPlaceService.getById(id);
    }

    @PostMapping
    public TicketPlace addTicket(TicketPlace ticketPlace) {
        return ticketPlaceService.createTicket(ticketPlace);
    }


    @PutMapping("/buy/{id}")
    public void buyTicket(@PathVariable String id) {
        ticketPlaceService.buyTicket(id);
    }

    @PutMapping("/pay/{id}")
    public boolean payTicket(@PathVariable String id, @RequestParam BigDecimal money) {
        return ticketPlaceService.payTicket(id, money);
    }

}
