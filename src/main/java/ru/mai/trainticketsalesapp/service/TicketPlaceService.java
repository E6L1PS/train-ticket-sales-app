package ru.mai.trainticketsalesapp.service;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import ru.mai.trainticketsalesapp.exception.NotFoundException;
import ru.mai.trainticketsalesapp.model.TicketPlace;
import ru.mai.trainticketsalesapp.model.Train;
import ru.mai.trainticketsalesapp.repository.TicketPlaceRepository;
import ru.mai.trainticketsalesapp.repository.TrainRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TicketPlaceService {

    private final TicketPlaceRepository ticketPlaceRepository;

    private final TrainRepository trainRepository;

    public List<TicketPlace> getAll() {
        return ticketPlaceRepository.findAll();
    }

    public TicketPlace getById(String ticketId) {

        Optional<TicketPlace> ticket = ticketPlaceRepository.findById(ticketId);

        return ticket.orElse(null);
    }

    public List<TicketPlace> getTicketsByTrainId(String trainId) {
        Optional<Train> train = trainRepository.findById(trainId);
        return train.map(ticketPlaceRepository::findAllByTrain).orElseThrow(() -> new NotFoundException("Train " + trainId + " not found"));
    }

    public TicketPlace createTicket(TicketPlace ticketBody) {
        return ticketPlaceRepository.insert(TicketPlace.builder()
                .place(ticketBody.getPlace())
                .isFreePlace(ticketBody.getIsFreePlace())
                .build());

    }
}
