package ru.mai.trainticketsalesapp.controller;

import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.mai.trainticketsalesapp.dto.TrainDto;
import ru.mai.trainticketsalesapp.model.Train;
import ru.mai.trainticketsalesapp.model.TrainElastic;
import ru.mai.trainticketsalesapp.service.TrainService;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/train")
@RequiredArgsConstructor
public class TrainController {

    private final TrainService trainService;

    @GetMapping
    public Page<TrainElastic> getTrains(
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "5") Integer size
    ) {
        return trainService.findAll(PageRequest.of(page, size));
    }

    @GetMapping("/{id}")
    public TrainElastic getTrainById(@PathVariable String id) {
        return trainService.findById(id);
    }

    @GetMapping("/fuzzySearch/{approximateDepartureStation}")
    public List<TrainElastic> fuzzySearch(@PathVariable String approximateDepartureStation) throws IOException {
        SearchResponse<TrainElastic> searchResponse = trainService.fuzzySearch(approximateDepartureStation);
        List<Hit<TrainElastic>> hitList = searchResponse.hits().hits();
        List<TrainElastic> trains = new ArrayList<>();
        for (Hit<TrainElastic> hit : hitList) {
            trains.add(hit.source());
        }
        return trains;
    }

    @GetMapping("/s")
    public Page<TrainElastic> searchTrains(
            @RequestParam(required = false, defaultValue = "true") Boolean isFreePlace,
            @RequestParam String departureStation,
            @RequestParam String destinationStation,
            @RequestParam(required = false, defaultValue = "2023") Integer departureDateYear,
            @RequestParam Integer departureDateMonth,
            @RequestParam Integer departureDateDay,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "5") Integer size
    ) {
        return trainService.searchTrainsByDateAndStations(
                isFreePlace,
                departureStation,
                destinationStation,
                LocalDate.of(
                        departureDateYear,
                        departureDateMonth,
                        departureDateDay
                ),
                PageRequest.of(page, size));
    }

    @GetMapping("/s/date")
    public List<TrainElastic> searchTrains(
            @RequestParam(required = false) String departureDate,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "5") Integer size
    ) {
        return trainService.search(departureDate, PageRequest.of(page, size));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TrainElastic addTrain(@RequestBody TrainDto trainDto) {
        return trainService.createTrain(trainDto);
    }

    @PostMapping("/generate/{numberTrains}")
    @ResponseStatus(HttpStatus.CREATED)
    public void generateTrains(@PathVariable Long numberTrains) {
        trainService.generateAndSaveTrains(numberTrains);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TrainElastic updateTrain(@RequestBody Train train) {
        return trainService.updateTrain(train);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTrainById(@PathVariable String id) {
        trainService.deleteTrain(id);
    }

    @DeleteMapping("/all")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAll() {
        trainService.deleteAll();
    }

}
