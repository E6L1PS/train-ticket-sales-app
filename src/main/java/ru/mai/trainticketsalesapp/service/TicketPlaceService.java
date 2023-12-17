package ru.mai.trainticketsalesapp.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import ru.mai.trainticketsalesapp.exception.NotFoundException;
import ru.mai.trainticketsalesapp.model.TicketPlace;
import ru.mai.trainticketsalesapp.model.Train;
import ru.mai.trainticketsalesapp.model.TrainElastic;
import ru.mai.trainticketsalesapp.repository.TicketPlaceRepository;
import ru.mai.trainticketsalesapp.repository.TrainRepository;
import ru.mai.trainticketsalesapp.repository.TrainSearchRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class TicketPlaceService {

    private static final String LOCK_PREFIX = "ticket_lock:";
    private static final long LOCK_TIMEOUT_SECONDS = 60;

    private final TicketPlaceRepository ticketPlaceRepository;

    private final TrainRepository trainRepository;

    private final TrainSearchRepository trainSearchRepository;

    private final RedisTemplate<String, Object> redisTemplate;


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

//    public void buyTicket(String ticketId) {
//        Optional<TrainElastic> train = trainSearchRepository.findByTickets_IdEquals(ticketId);
//        if (train.isPresent()) {
//            TrainElastic t = train.get();
//            t.getTickets().stream().filter(t -> t.getId().equals(ticketId)).findAny()
//            .getTickets().
//            trainSearchRepository.save(train)
//        }
//        ticketPlaceRepository.updateIsFreePlace(ticketId, false);
//    }


    public void buyTicket(String ticketId) {
        String lockKey = LOCK_PREFIX + ticketId;

        TicketPlace ticketPlace = ticketPlaceRepository.findById(ticketId).orElseThrow(() -> new NotFoundException("Ticket not found"));

        if (ticketPlace != null && ticketPlace.getIsFreePlace()) {
            Boolean isLocked = redisTemplate.opsForValue().setIfAbsent(lockKey, true, LOCK_TIMEOUT_SECONDS, TimeUnit.SECONDS);
            if (Boolean.FALSE.equals(isLocked)) {
                log.info("Ticket {} locked.", ticketId);
                throw new NotFoundException("Ticket locked");
            }
        } else {
            log.info("Ticket {} sold.", ticketId);
            throw new NotFoundException("Ticket sold");
        }
    }

    public boolean payTicket(String ticketId, BigDecimal money) {
        String lockKey = LOCK_PREFIX + ticketId;
        TicketPlace ticketPlace = ticketPlaceRepository.findById(ticketId).orElseThrow(() -> new NotFoundException("Ticket not found"));
        TrainElastic train = trainSearchRepository.findByTickets_IdEquals(ticketId).orElseThrow(() -> new NotFoundException("Ticket not found"));

        if (Objects.equals(redisTemplate.opsForValue().get(lockKey), true)) {
            if (money.compareTo(ticketPlace.getPrice()) >= 0) {
                updateIsFreePlaceInTrainElastic(train, ticketId);
                ticketPlace.setIsFreePlace(false);
                ticketPlaceRepository.save(ticketPlace);
                trainSearchRepository.save(train);
                unlockTicket(ticketId);
                log.info("Ticket {} sold.", ticketId);
                return true;
            } else {
                log.info("Not enough money for ticket {}.", ticketId);
                return false;
            }
        }

        log.info("Error when trying to pay for a ticket {}", ticketId);
        return false;
    }

    private void updateIsFreePlaceInTrainElastic(TrainElastic train, String ticketId) {
        List<TicketPlace> tickets = train.getTickets();
        for (TicketPlace ticket : tickets) {
            if (ticket.getId().equals(ticketId)) {
                ticket.setIsFreePlace(false);
                break;
            }
        }
    }
//
//
//    public boolean buyTicket(String ticketId) {
//        String lockKey = LOCK_PREFIX + ticketId;
//
//        try {
//            Boolean isLocked = redisTemplate.opsForValue().setIfAbsent(lockKey, true, LOCK_TIMEOUT_SECONDS, TimeUnit.SECONDS);
//
//            if (isLocked != null && isLocked) {
//                // Получение информации о билете
//                TicketPlace ticketPlace = ticketPlaceRepository.findById(ticketId).orElse(null);
//
//                if (ticketPlace != null && ticketPlace.getIsFreePlace()) {
//                    // Место свободно, помечаем его как купленное
//                    ticketPlace.setIsFreePlace(false);
//                    ticketPlaceRepository.save(ticketPlace);
//
//                    return true;
//                }
//            }
//        } finally {
//            unlockTicket(ticketId);
//        }
//
//        return false;
//    }

    private void unlockTicket(String ticketId) {
        String lockKey = LOCK_PREFIX + ticketId;
        redisTemplate.delete(lockKey);
    }

}
