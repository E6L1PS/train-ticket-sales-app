package ru.mai.trainticketsalesapp.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.mai.trainticketsalesapp.model.TicketPlace;
import ru.mai.trainticketsalesapp.service.TicketPlaceService;

import java.util.List;

@RestController
@RequestMapping("api/v1/ticket")
@RequiredArgsConstructor
public class TicketPlaceController {
    private final TicketPlaceService ticketPlaceService;

    @GetMapping
    public List<TicketPlace> getAll() {
        return ticketPlaceService.getAll();
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


    @PutMapping("/{id}")
    public void buyTicket(@PathVariable String id) {
       // ticketPlaceService.buyTicket(id);
    }


}
