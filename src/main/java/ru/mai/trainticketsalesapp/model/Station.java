package ru.mai.trainticketsalesapp.model;

import lombok.*;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;


@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Station implements Serializable {

    @Field(type = FieldType.Text)
    private String name;

}
