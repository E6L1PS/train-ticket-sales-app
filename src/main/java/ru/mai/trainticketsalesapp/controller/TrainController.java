package ru.mai.trainticketsalesapp.controller;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.web.bind.annotation.*;
import ru.mai.trainticketsalesapp.dto.TrainDto;
import ru.mai.trainticketsalesapp.mapper.TrainMapper;
import ru.mai.trainticketsalesapp.model.Train;
import ru.mai.trainticketsalesapp.service.TrainService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/train")
@RequiredArgsConstructor
public class TrainController {

    private final TrainService trainService;
    private final TrainMapper trainMapper;

    @GetMapping
    public List<TrainDto> getTrains() {
        return trainMapper.toDTO(trainService.getAll());
    }

    @PostMapping
    public Train addTrain(@RequestBody TrainDto trainDto) {
        return trainService.createTrain(trainDto);
    }

    @PutMapping
    public Train updateTrain(@RequestBody TrainDto trainDto) {
        return trainService.createTrain(trainDto);
    }

    @DeleteMapping
    public void deleteTrain(@RequestBody ObjectId id) {
        trainService.deleteTrain(id);
    }

}
