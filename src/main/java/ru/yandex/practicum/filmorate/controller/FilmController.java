package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import javax.validation.Valid;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/films")
public class FilmController {
    private long lastFilmId = 0;
    private static HashMap<Long, Film> films = new HashMap<>();
    private final static Logger log = LoggerFactory.getLogger(FilmController.class);
    private final static LocalDate DATE_BORN_MOVIE = LocalDate.of(1895, Month.DECEMBER, 28);

    @GetMapping
    public List<Film> findAll() {
       return new ArrayList<>(films.values());
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) throws ValidationException {
        String message = check(film);
        if (message.isBlank()) {
            film.setId(getNextId());
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
        if (film == null) {
            message = "Данные о фильме не заполнены.";
        } else if (film.getName() == null || film.getName().isBlank()) {
            message = "Название фильма не может быть пустым.";
        } else if (film.getDescription().length() > 200) {
            message = "Превышена максимальная длина описания — 200 символов";
        } else if (film.getReleaseDate().isBefore(DATE_BORN_MOVIE)) {
            message = "Дата релиза не может быть раньше даты 28.12.1895";
        } else if (film.getDuration() <= 0) {
            message = "Продолжительность фильма должна быть больше 0";
        }
        return message;
    }

    private long getNextId() {
        return ++lastFilmId;
    }

    //метод для тестов
    public void deleteAll() {
        films.clear();
    }

}
