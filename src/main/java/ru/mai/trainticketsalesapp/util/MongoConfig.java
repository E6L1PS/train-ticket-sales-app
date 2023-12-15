//
//package ru.mai.trainticketsalesapp.util;
//
//import com.github.javafaker.Faker;
//import lombok.RequiredArgsConstructor;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
//import org.springframework.data.mongodb.core.MongoTemplate;
//import ru.mai.trainticketsalesapp.model.Route;
//import ru.mai.trainticketsalesapp.model.Station;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@Configuration
//@RequiredArgsConstructor
//public class MongoConfig {
//
//    private final MongoTemplate mongoTemplate;
//
//    @Bean
//    public CommandLineRunner initData() {
//        return args -> mongoTemplate.insertAll(generateRandomRoutes());
//    }
//
//    private List<Route> generateRandomRoutes() {
//        Faker faker = new Faker();
//        List<Route> routes = new ArrayList<>();
//
//        for (int i = 0; i < 500; i++) {
//
//
//            List<Station> stations = new ArrayList<>();
//            for (int j = 0; j < faker.random().nextInt(10); j++) {
//                stations.add(Station.builder()
//                        .name(faker.address().city())
//                        .build());
//            }
//
//            routes.add(Route.builder()
//                    .numberRoute("#" + i)
//                    .stations(stations)
//                    .build());
//        }
//
//        return routes;
//    }
//}
