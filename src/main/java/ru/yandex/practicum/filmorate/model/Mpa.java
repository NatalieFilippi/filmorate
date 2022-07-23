package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.*;

@Data
@AllArgsConstructor
public class Mpa {
    private int id;
    private String name;

    @JsonCreator
    public Mpa(int id) {
        this.id = id;
    }
}
