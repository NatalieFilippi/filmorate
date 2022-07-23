package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
//todo delete
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
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

    @JsonCreator
    public Film(String name, String description, LocalDate releaseDate,
                Integer duration, Set<Director> director, Mpa mpa, Set<Genre> genres)
    {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.directors = director;
        this.likes = new HashSet<>();
        this.mpa = mpa;
        this.genres = genres;
    }
}
