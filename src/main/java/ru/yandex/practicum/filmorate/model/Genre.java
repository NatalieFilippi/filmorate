package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.*;

@Data
@AllArgsConstructor
public class Genre {
    private int id;
    private String name;

    @JsonCreator
    public Genre(int id) {
        this.id = id;
    }
}
