package ru.mai.trainticketsalesapp.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import ru.mai.trainticketsalesapp.dto.CountTrains;
import ru.mai.trainticketsalesapp.dto.TrainDto;
import ru.mai.trainticketsalesapp.mapper.TrainMapper;
import ru.mai.trainticketsalesapp.model.Train;
import ru.mai.trainticketsalesapp.model.TrainElastic;
import ru.mai.trainticketsalesapp.service.TrainService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/train")
@RequiredArgsConstructor
public class TrainController {

    private final TrainService trainService;
    private final TrainMapper trainMapper;

    @GetMapping("/s")
    public Page<TrainElastic> searchTrains(
            @RequestParam String departureStation,
            @RequestParam String destinationStation,
            @RequestParam(required = false, defaultValue = "2023") Integer departureDateYear,
            @RequestParam Integer departureDateMonth,
            @RequestParam Integer departureDateDay,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "5") Integer size
    ) {
        return trainService.searchTrainsByDateAndStations(
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

    @GetMapping
    public Page<TrainElastic> getTrains(
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "5") Integer size
    ) {
        return trainService.findAll(PageRequest.of(page, size));
    }

    @GetMapping("/count")
    public CountTrains getCountTrains() {
        return CountTrains.builder()
                .mongo(trainService.countTrains())
                .elastic(trainService.countTrainsElastic())
                .build();
    }

    @PostMapping
    public Train addTrain(@RequestBody TrainDto trainDto) {
        return trainService.createTrain(trainDto);
    }


    @PostMapping("/generate/{numberTrains}")
    public void generateTrains(@PathVariable Long numberTrains) {
        trainService.generateAndSaveTrains(numberTrains);
    }

    @PutMapping
    public Train updateTrain(@RequestBody TrainDto trainDto) {
        return trainService.createTrain(trainDto);
    }

    @DeleteMapping
    public void deleteTrain(@RequestBody String id) {
        trainService.deleteTrain(id);
    }

    @DeleteMapping("/all")
    public void deleteAll() {
        trainService.deleteAll();
    }

}
