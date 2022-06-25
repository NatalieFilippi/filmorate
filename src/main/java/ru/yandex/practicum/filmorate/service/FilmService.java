package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.interfaces.FilmStorage;
import ru.yandex.practicum.filmorate.interfaces.UserStorage;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

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
        return filmStorage.create(film);
    }

    public Film put(Film film) throws ValidationException, ObjectNotFoundException {
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
        film.addLike(user);
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
        film.deleteLike(user);
        return film;
    }

    public List<Film> findByRating(int count) {
        return filmStorage.findAll().stream()
                .sorted((o1, o2) -> {
                    if(o1.getLikes().size() == o2.getLikes().size())
                        return 0;
                    else if(o1.getLikes().size() > o2.getLikes().size())
                        return -1;
                    else return 1;
                })
                .limit(count)
                .collect(Collectors.toList());
    }

    public void deleteAll() {
        filmStorage.deleteAll();
    }

}
