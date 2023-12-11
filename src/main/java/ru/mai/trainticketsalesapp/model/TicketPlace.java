package ru.mai.trainticketsalesapp.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;


@Document(collection = "tickets")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TicketPlace {
    @Id
    private String id;

    @Field(type = FieldType.Integer)
    private Integer place;

    @Field(type = FieldType.Double)
    private BigDecimal price;

    @Field(type = FieldType.Boolean, name = "is_free_place")
    private Boolean isFreePlace;


}
