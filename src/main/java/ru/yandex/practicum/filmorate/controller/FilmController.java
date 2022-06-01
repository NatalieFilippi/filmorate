package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.time.Month;
import java.util.Collection;
import java.util.HashMap;

@RestController
@RequestMapping("/films")
public class FilmController {
    HashMap<Long, Film> films = new HashMap<>();
    private final static Logger log = LoggerFactory.getLogger(FilmController.class);

    @GetMapping
    public Collection<Film> findAll() {
       return films.values();
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) throws ValidationException {
        String message = check(film);
        if (message.isBlank()) {
            films.put(film.getId(), film);
        } else {
            log.debug(message);
            throw new ValidationException(message);
        }
        log.debug("Сохранён фильм: {}", film.toString());
        return film;
    }

    @PutMapping
    public Film put(@Valid @RequestBody Film film) throws ValidationException {
        String message = check(film);
        if (!films.containsKey(film.getId())) {
            throw new ValidationException("Фильм не найден.");
        }
        if (message.isBlank()) {
            films.put(film.getId(), film);
        } else {
            log.debug(message);
            throw new ValidationException(message);
        }
        log.debug("Обновлён фильм: {}", film.toString());
        return film;
    }

    private String check(Film film) throws ValidationException {
        String message = "";
        if (film.getName() == null || film.getName().isBlank()) {
            message = "Название фильма не может быть пустым.";
        }
        if (film.getDescription().length() > 200) {
            message = "Превышена максимальная длина описания — 200 символов";
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, Month.DECEMBER, 28))) {
            message = "Дата релиза не может быть раньше даты 28.12.1895";
        }
        if (film.getDuration() <= 0) {
            message = "Продолжительность фильма должна быть больше 0";
        }
        if (film.getId() == 0) {
            film.setId(getIncrement());
        }
        return message;
    }

    private long getIncrement() {
        long increment = 1;
        if (!films.isEmpty()) {
            increment = films.keySet().stream().max(Long::compare).get();
        }
        return increment;
    }
}
