package ru.mai.trainticketsalesapp.service;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.mai.trainticketsalesapp.exception.NotFoundException;
import ru.mai.trainticketsalesapp.model.TicketPlace;
import ru.mai.trainticketsalesapp.model.Train;
import ru.mai.trainticketsalesapp.repository.TicketPlaceRepository;
import ru.mai.trainticketsalesapp.repository.TrainRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class TicketPlaceServiceTest {

    @Mock
    private TrainRepository trainRepository;

    @Mock
    private TicketPlaceRepository ticketPlaceRepository;

    @InjectMocks
    private TicketPlaceService ticketPlaceService;
    private List<TicketPlace> tickets;
    private List<Train> trains;

    @BeforeEach
    void setUp() {

        tickets = List.of(
                TicketPlace.builder()
                        .id("0")
                        .build(),
                TicketPlace.builder()
                        .id("1")
                        .build(),
                TicketPlace.builder()
                        .id("2")
                        .build(),
                TicketPlace.builder()
                        .id("3")
                        .build(),
                TicketPlace.builder()
                        .id("4")
                        .build(),
                TicketPlace.builder()
                        .id("5")
                        .build()
        );

        trains = List.of(
                Train.builder()
                        .id("0")
                        .tickets(tickets.subList(0, 2))
                        .build(),
                Train.builder()
                        .id("1")
                        .tickets(tickets.subList(3, 4))
                        .build(),
                Train.builder()
                        .id("2")
                        .tickets(tickets.subList(5, 6))
                        .build()
        );

    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getAll_ShouldReturnListTickets() {

    }

    @Test
    void getById_ShouldReturnTicket() {
    }

    @Test
    void getTicketsByTrainId_ShouldReturnListTicketsByTrainId() {
        Mockito.when(
                trainRepository.findById(Mockito.any(String.class))
        ).thenReturn(
                trains.stream().filter(train -> train.getId().equals("0")).findFirst()
        );

        Mockito.when(
                ticketPlaceRepository.findAllByTrain(Mockito.any(Train.class))
        ).thenReturn(
                tickets.subList(0, 2)
        );

        List<TicketPlace> result = ticketPlaceService.getTicketsByTrainId("0");

        Assertions.assertEquals(2, result.size());
    }

    @Test
    void getTicketsByTrainId_ShouldReturnNotFoundException() {
        Mockito.when(
                trainRepository.findById(Mockito.any(String.class))
        ).thenReturn(
                Optional.empty()
        );

        Assertions.assertThrows(NotFoundException.class, () ->
            ticketPlaceService.getTicketsByTrainId("0")
        );

    }

    @Test
    void createTicket() {
    }
}
