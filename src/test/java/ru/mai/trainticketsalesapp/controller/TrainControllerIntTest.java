package ru.mai.trainticketsalesapp.controller;

import com.redis.testcontainers.RedisContainer;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpStatus;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import ru.mai.trainticketsalesapp.dto.TrainDto;
import ru.mai.trainticketsalesapp.model.*;
import ru.mai.trainticketsalesapp.repository.RouteRepository;
import ru.mai.trainticketsalesapp.repository.TicketPlaceRepository;
import ru.mai.trainticketsalesapp.repository.TrainRepository;
import ru.mai.trainticketsalesapp.repository.TrainSearchRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Testcontainers
@TestConfiguration(proxyBeanMethods = false)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TrainControllerIntTest {

    @LocalServerPort
    private Integer port;

    @Container
    @ServiceConnection
    private static final MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:latest"));

    @Container
    @ServiceConnection
    private static final RedisContainer redisContainer = new RedisContainer(DockerImageName.parse("redis:latest"));

    @Container
    @ServiceConnection
    private static final ElasticsearchContainer elasticsearchContainer =
            new ElasticsearchContainer(DockerImageName.parse("elasticsearch:8.10.2"))
                    .withEnv("xpack.security.enabled", "false")
                    .withEnv("http.cors.enabled", "true")
                    .withEnv("ES_JAVA_OPTS", "-Xmx256m -Xms256m");
    @Autowired
    RouteRepository routeRepository;
    @Autowired
    TicketPlaceRepository ticketPlaceRepository;

    @Autowired
    TrainRepository trainRepository;

    @Autowired
    TrainSearchRepository trainSearchRepository;

    List<TicketPlace> tickets;
    List<Station> stations;
    Route route;
    Train train;

    TrainDto trainDto;
    List<Train> trains;

    @BeforeEach
    void setup() {
        tickets = IntStream.range(1, 21).mapToObj(
                i -> TicketPlace.builder()
                        .price(BigDecimal.valueOf(4500))
                        .isFreePlace(true)
                        .place(i)
                        .build()
        ).collect(Collectors.toList());

        stations = List.of(
                Station.builder().name("A").build(),
                Station.builder().name("B").build()
        );

        route = Route.builder()
                .stations(stations)
                .numberRoute("#1")
                .build();

        train = Train.builder()
                .tickets(tickets)
                .route(route)
                .departureDate(LocalDate.now())
                .placeCount(3)
                .build();

        trainDto = TrainDto.builder()
                .route(route)
                .placeCount(20)
                .priceForPlace(BigDecimal.valueOf(5000))
                .departureDate(LocalDate.now())
                .build();

        trains = List.of(
                train,
                train
        );
        String endPoint = "/api/v1/train";
        RestAssured.baseURI = "http://localhost:" + port + endPoint;
        trainRepository.deleteAll();
        ticketPlaceRepository.deleteAll();
        routeRepository.deleteAll();
        trainSearchRepository.deleteAll();
    }


    @Test
    public void testEmptyGetAll() {
        RestAssured.given()
                .contentType(ContentType.JSON)
                .when()
                .get()
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("content", Matchers.hasSize(0));
    }

    @Test
    public void testNotEmptyGetAll() {
        List<TicketPlace> ticketsByRepo = ticketPlaceRepository.insert(tickets);
        Route routeByRepo = routeRepository.save(route);
        trainSearchRepository.saveAll(List.of(
                TrainElastic.builder()
                        .tickets(ticketsByRepo)
                        .route(routeByRepo)
                        .placeCount(20)
                        .departureDate(LocalDate.now())
                        .build()
        ));

        RestAssured.given()
                .contentType(ContentType.JSON)
                .when()
                .get()
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("content", Matchers.hasSize(1));
    }

    @Test
    public void testNotEmptySearch() {
        TrainElastic trainElastic = TrainElastic.builder()
                .tickets(tickets)
                .route(route)
                .departureDate(LocalDate.now())
                .build();

        trainSearchRepository.save(trainElastic);

        RestAssured.given()
                .contentType(ContentType.JSON)
                .param("departureStation", "A")
                .param("destinationStation", "B")
                .param("departureDateMonth", LocalDate.now().getMonthValue())
                .param("departureDateDay", LocalDate.now().getDayOfMonth())
                .when()
                .get("/s")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("content", Matchers.hasSize(1));
    }


    @Test
    public void testCreate() {
        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(trainDto)
                .when()
                .post()
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .body("id", Matchers.notNullValue())
                .body("tickets", Matchers.hasSize(trainDto.getPlaceCount()));
    }

    @Test
    public void testGenerateTrains() {
        long countTrains = 5;

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(trainDto)
                .when()
                .post("/generate/{numberTrains}", countTrains)
                .then()
                .statusCode(HttpStatus.CREATED.value());

        long countTrainsByRepo = trainRepository.count();
        long countTrainsByElasticRepo = trainSearchRepository.count();
        Assertions.assertEquals(countTrainsByRepo, countTrainsByElasticRepo, countTrains);
    }

    @Test
    public void testUpdate() {
        List<TicketPlace> ticketsByRepo = ticketPlaceRepository.insert(tickets);
        Route routeByRepo = routeRepository.save(route);
        Train trainByRepo = trainRepository.save(
                Train.builder()
                        .placeCount(20)
                        .route(routeByRepo)
                        .tickets(ticketsByRepo)
                        .build());
        trainByRepo.setPlaceCount(10);
        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(trainByRepo)
                .when()
                .put()
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .body("id", Matchers.notNullValue())
                .body("placeCount", Matchers.equalTo(trainByRepo.getPlaceCount()));
    }

    @Test
    public void testDeleteAll() {
        ticketPlaceRepository.insert(tickets);
        RestAssured.given()
                .contentType(ContentType.JSON)
                .when()
                .delete("/all")
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());
        List<TicketPlace> ticketsByRepo = ticketPlaceRepository.findAll();
        Assertions.assertEquals(ticketsByRepo.size(), 0);
    }

    @Test
    public void testDeleteById() {
        RestAssured.given()
                .contentType(ContentType.JSON)
                .when()
                .delete("/{id}", "test")
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }


}

