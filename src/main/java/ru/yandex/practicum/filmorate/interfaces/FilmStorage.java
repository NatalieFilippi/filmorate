package ru.yandex.practicum.filmorate.interfaces;

import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import javax.validation.Valid;
import java.util.List;

public interface FilmStorage {

    List<Film> findAll();
    Film findById(long id) throws ObjectNotFoundException;
    Film create(Film film) throws ValidationException;
    Film put(Film film) throws ValidationException, ObjectNotFoundException;
    void deleteAll();
    Film delete(Film film) throws ValidationException, ObjectNotFoundException;
    boolean addLike(long filmId, long userId);
    boolean deleteLike(long filmId, long userId);
    List<Film> getPopularFilms(int count);

    Mpa findMpaById(long id) throws ObjectNotFoundException;

    List<Mpa> findAllMpa();

    Genre findGenreById(long id) throws ObjectNotFoundException;

    List<Genre> findAllGenre();
}
