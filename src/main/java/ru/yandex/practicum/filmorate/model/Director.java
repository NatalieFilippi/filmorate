package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.*;


@Data
@AllArgsConstructor
public class Director {
    private int id;
    private String name;

    @JsonCreator
    public Director(int id) {
        this.id = id;
    }
}