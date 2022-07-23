package ru.yandex.practicum.filmorate.model;

import lombok.*;

import java.time.LocalDate;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
public class Film {
    @EqualsAndHashCode.Include private long id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private Integer duration;
    private Set<Director> directors;
    private Set<Integer> likes;
    private Mpa mpa;
    private Set<Genre> genres;
}
