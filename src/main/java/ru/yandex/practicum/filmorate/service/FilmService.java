package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.interfaces.FilmStorage;
import ru.yandex.practicum.filmorate.interfaces.UserStorage;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final static LocalDate DATE_BORN_MOVIE = LocalDate.of(1895, Month.DECEMBER, 28);
    private final static Logger log = LoggerFactory.getLogger(FilmController.class);

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public List<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film findById(long id) throws ObjectNotFoundException {
        return filmStorage.findById(id);
    }

    public Film create(Film film) throws ValidationException {
        String message = check(film);
        if (!message.isBlank()) {
            log.debug(message);
            throw new ValidationException(message);
        }
        return filmStorage.create(film);
    }

    public Film put(Film film) throws ValidationException, ObjectNotFoundException {
        String message = check(film);
        if (!message.isBlank()) {
            log.debug(message);
            throw new ValidationException(message);
        }
        return filmStorage.put(film);
    }

    public Film addLike(long filmId, long userId) throws ObjectNotFoundException {
        Film film = filmStorage.findById(filmId);
        User user = userStorage.findById(userId);
        if (film == null) {
            throw new ObjectNotFoundException(String.format("Фильм с id %d не найден", filmId));
        }
        if (user == null) {
            throw new ObjectNotFoundException(String.format("Пользователь с id %d не найден", userId));
        }
        if (filmStorage.addLike(filmId, userId)) {
            log.debug(String.format("Пользователь %d лайкнул фильм %d",
                                    user.getId(), film.getId()));
        };
        return film;
    }

    public Film deleteLike(long filmId, long userId) throws ObjectNotFoundException {
        Film film = filmStorage.findById(filmId);
        User user = userStorage.findById(userId);
        if (film == null) {
            throw new ObjectNotFoundException(String.format("Фильм с id %d не найден", filmId));
        }
        if (user == null) {
            throw new ObjectNotFoundException(String.format("Пользователь с id %d не найден", userId));
        }
        filmStorage.deleteLike(filmId, userId);
        return film;
    }

    public List<Film> getPopularFilms(int count) {
        return filmStorage.getPopularFilms(count);
    }

    public void deleteAll() {
        filmStorage.deleteAll();
    }

    public void delete(Film film) throws ValidationException, ObjectNotFoundException {
        String message = check(film);
        if (!message.isBlank()) {
            log.debug(message);
            throw new ValidationException(message);
        }
        filmStorage.delete(film);
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

    public Mpa findMpaById(long id) throws ObjectNotFoundException {
        return filmStorage.findMpaById(id);
    }

    public List<Mpa> findAllMpa() {
        return filmStorage.findAllMpa();
    }

    public Genre findGenreById(long id) throws ObjectNotFoundException {
        return filmStorage.findGenreById(id);
    }

    public List<Genre> findAllGenre() {
        return filmStorage.findAllGenre();
    }
}
