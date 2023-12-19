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
import ru.mai.trainticketsalesapp.model.Station;
import ru.mai.trainticketsalesapp.repository.StationRepository;

import java.util.List;

@Testcontainers
@TestConfiguration(proxyBeanMethods = false)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StationControllerIntTest {

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
    StationRepository stationRepository;
    Station station;
    List<Station> stations;

    @BeforeEach
    void setup() {
        station = Station.builder().name("ABC").build();

        stations = List.of(
                Station.builder().name("A").build(),
                Station.builder().name("B").build()
        );

        String endPoint = "/api/v1/station";
        RestAssured.baseURI = "http://localhost:" + port + endPoint;
        stationRepository.deleteAll();
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
        stationRepository.saveAll(stations);

        RestAssured.given()
                .contentType(ContentType.JSON)
                .when()
                .get()
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("content", Matchers.hasSize(2));
    }

    @Test
    public void testCreate() {
        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(station)
                .when()
                .post()
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .body("name", Matchers.equalTo(station.getName()));
    }

}

