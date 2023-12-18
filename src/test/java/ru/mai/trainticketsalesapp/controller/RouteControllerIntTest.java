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
import ru.mai.trainticketsalesapp.model.Route;
import ru.mai.trainticketsalesapp.model.Station;
import ru.mai.trainticketsalesapp.repository.RouteRepository;

import java.util.List;

@Testcontainers
@TestConfiguration(proxyBeanMethods = false)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RouteControllerIntTest {

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

    @BeforeEach
    void setup() {
        RestAssured.baseURI = "http://localhost:" + port;
        routeRepository.deleteAll();
    }


    @Test
    public void testEmptyGetAll() {
        RestAssured.given()
                .contentType(ContentType.JSON)
                .when()
                .get("/api/v1/route")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("content", Matchers.hasSize(0));

    }

    @Test
    public void testNotEmptyGetAll() {
        List<Station> stations = List.of(
                Station.builder().name("A").build(),
                Station.builder().name("B").build()
        );
        List<Route> routes = List.of(
                Route.builder()
                        .stations(stations)
                        .numberRoute("#1")
                        .build(),
                Route.builder()
                        .stations(stations)
                        .numberRoute("#2")
                        .build()
        );
        routeRepository.saveAll(routes);

        RestAssured.given()
                .contentType(ContentType.JSON)
                .when()
                .get("/api/v1/route")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("content", Matchers.hasSize(2));
    }

    @Test
    public void testEmptyGetById() {
        String id = "test";

        RestAssured.given()
                .contentType(ContentType.JSON)
                .when()
                .get("/api/v1/route/{id}", id)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

//    @Test
//    public void testNotEmptyGetById() throws JsonProcessingException {
//        List<Station> stations = List.of(
//                Station.builder().name("A").build(),
//                Station.builder().name("B").build()
//        );
//        Route route = routeRepository.save(Route.builder()
//                .stations(stations)
//                .numberRoute("#1")
//                .build());
//
//        RestAssured.given()
//                .contentType(ContentType.JSON)
//                .when()
//                .get("/api/v1/route/{id}", route.getId())
//                .then()
//                .statusCode(HttpStatus.OK.value())
//                .body(Matchers.contains(
//                        new ObjectMapper()
//                                .writer()
//                                .withDefaultPrettyPrinter()
//                                .writeValueAsString(route)));
//    }

}

