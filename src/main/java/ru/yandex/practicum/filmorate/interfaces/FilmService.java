package ru.yandex.practicum.filmorate.interfaces;

import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;
import java.util.Optional;

public interface FilmService {
    List<Film> findAllFilms();
    Film findById(long id) throws ObjectNotFoundException;
    Film create(Film film) throws ValidationException;
    Film put(Film film) throws ValidationException, ObjectNotFoundException;
    void deleteAll();
    void deleteFilm(long id) throws ValidationException, ObjectNotFoundException;


}
