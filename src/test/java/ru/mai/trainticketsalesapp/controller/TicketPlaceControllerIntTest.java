package ru.mai.trainticketsalesapp.controller;

import com.redis.testcontainers.RedisContainer;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
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
public class TicketPlaceControllerIntTest {

    @LocalServerPort
    private Integer port;


    @Container
    @ServiceConnection
    private static MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:latest"));

    @Container
    @ServiceConnection
    private static RedisContainer redisContainer = new RedisContainer(DockerImageName.parse("redis:latest"));

    @Container
    @ServiceConnection
    private static ElasticsearchContainer elasticsearchContainer =
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
    List<Train> trains;

    @BeforeEach
    void setup() {
        tickets = IntStream.range(1, 26).mapToObj(
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

        trains = List.of(
                train,
                train
        );
        String endPoint = "/api/v1/ticket";
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
        int size = ticketsByRepo.size();

        RestAssured.given()
                .contentType(ContentType.JSON)
                .when()
                .get()
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("content", Matchers.hasSize(size >= 5 ? 5 : size % 5));
    }

    @Test
    public void testEmptyGetById() {
        String id = "test";

        RestAssured.given()
                .contentType(ContentType.JSON)
                .when()
                .get("/{id}", id)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body("message", Matchers.containsString("not found"));
    }

    @Test
    public void testNotEmptyGetById() {
        TicketPlace ticketByRepo = ticketPlaceRepository.save(tickets.get(0));

        RestAssured.given()
                .contentType(ContentType.JSON)
                .when()
                .get("/{id}", ticketByRepo.getId())
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", Matchers.equalTo(ticketByRepo.getId()))
                .body("place", Matchers.equalTo(ticketByRepo.getPlace()))
                //  .body("price", Matchers.equalTo(ticketByRepo.getPrice()))
                .body("isFreePlace", Matchers.equalTo(ticketByRepo.getIsFreePlace()));
    }

    @Test
    public void testEmptyGetByTrainId() {
        String id = "test";

        RestAssured.given()
                .contentType(ContentType.JSON)
                .when()
                .get("/train/{id}", id)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body("message", Matchers.containsString("not found"));
    }


    @Test
    public void testNotEmptyGetByTrainId() {
        List<TicketPlace> ticketsByRepo = ticketPlaceRepository.insert(tickets);
        Train trainByRepo = trainRepository.save(Train.builder().tickets(ticketsByRepo).build());

        RestAssured.given()
                .contentType(ContentType.JSON)
                .when()
                .get("/train/{id}", trainByRepo.getId())
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("content", Matchers.hasSize(ticketsByRepo.size()));
    }

    @Test
    public void testCreate() {
        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(tickets.get(0))
                .when()
                .post()
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .body("id", Matchers.notNullValue());
    }

    @Test
    public void testEmptyBuyById() {
        String id = "test";
        RestAssured.given()
                .contentType(ContentType.JSON)
                .when()
                .put("/buy/{id}", id)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body("message", Matchers.containsString("not found"));
    }

    @Test
    public void testNotEmptyBuyById() {
        TicketPlace ticketByRepo = ticketPlaceRepository.insert(tickets.get(0));
        RestAssured.given()
                .contentType(ContentType.JSON)
                .when()
                .put("/buy/{id}", ticketByRepo.getId())
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    public void testNotEmptyAndSoldTicketBuyById() {
        TicketPlace ticketByRepo = ticketPlaceRepository.insert(TicketPlace.builder().isFreePlace(false).build());
        RestAssured.given()
                .contentType(ContentType.JSON)
                .when()
                .put("/buy/{id}", ticketByRepo.getId())
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body("message", Matchers.containsString("Ticket sold"));
    }

    @Test
    public void testBuyLocked() {
        TicketPlace ticketByRepo = ticketPlaceRepository.insert(tickets.get(0));
        RestAssured.given()
                .contentType(ContentType.JSON)
                .when()
                .put("/buy/{id}", ticketByRepo.getId())
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

        RestAssured.given()
                .contentType(ContentType.JSON)
                .when()
                .put("/buy/{id}", ticketByRepo.getId())
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body("message", Matchers.containsString("Ticket locked"));
    }

    @Test
    public void testPayFail() {
//        TicketPlace ticketByRepo = ticketPlaceRepository.insert(tickets.get(0));
        List<TicketPlace> ticketsByRepo = ticketPlaceRepository.insert(tickets);
        Train train = Train.builder().tickets(ticketsByRepo).build();
        TrainElastic trainElastic = TrainElastic.builder().tickets(ticketsByRepo).build();
        Train trainByRepo = trainRepository.save(train);
        TrainElastic trainElasticRepo = trainSearchRepository.save(trainElastic);
        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(tickets.get(0))
                .param("money", 5000.0)
                .when()
                .put("/pay/{id}", ticketsByRepo.get(0).getId())
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .body(Matchers.equalTo("false"));
    }

    @Test
    public void testPayOk() {
        List<TicketPlace> ticketsByRepo = ticketPlaceRepository.insert(tickets);
        Train train = Train.builder().tickets(ticketsByRepo).build();
        TrainElastic trainElastic = TrainElastic.builder().tickets(ticketsByRepo).build();
        Train trainByRepo = trainRepository.save(train);
        TrainElastic trainElasticRepo = trainSearchRepository.save(trainElastic);

        String id = ticketsByRepo.get(0).getId();

        RestAssured.given()
                .contentType(ContentType.JSON)
                .when()
                .put("/buy/{id}", id)
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(tickets.get(0))
                .param("money", 5000.0)
                .when()
                .put("/pay/{id}", id)
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .body(Matchers.equalTo("true"));
    }

}

