package ru.yandex.practicum.filmorate.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.interfaces.FilmStorage;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
public class InMemoryFilmStorage implements FilmStorage {

    private long lastFilmId = 0;
    private static HashMap<Long, Film> films = new HashMap<>();
    private final static LocalDate DATE_BORN_MOVIE = LocalDate.of(1895, Month.DECEMBER, 28);
    private final static Logger log = LoggerFactory.getLogger(FilmController.class);

    @Override
    public List<Film> findAll() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film findById(long id) throws ObjectNotFoundException {
        if (!films.containsKey(id)) {
            throw new ObjectNotFoundException("Фильм не найден!");
        }
        return films.get(id);
    }

    @Override
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

    @Override
    public Film put(@Valid @RequestBody Film film) throws ValidationException, ObjectNotFoundException {
        String message = check(film);
        if (!films.containsKey(film.getId())) {
            throw new ObjectNotFoundException("Фильм не найден.");
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

    @Override
    public void deleteAll() {
        films.clear();
    }

    @Override
    public Film delete(@Valid @RequestBody Film film) throws ValidationException {
        String message = check(film);
        if (message.isBlank()) {
            log.debug("Удалён фильм: {}", film.toString());
            return films.remove(film.getId());
        } else {
            log.debug(message);
            throw new ValidationException(message);
        }
    }


    //ДОПОЛНИТЕЛЬНЫЕ МЕТОДЫ
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
}
