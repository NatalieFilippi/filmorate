package ru.yandex.practicum.filmorate.interfaces;

import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;
import java.util.Map;

public interface FilmService {
    List<Film> findAll();
    Film findById(long id) throws ObjectNotFoundException;
    Film create(Film film) throws ValidationException;
    Film put(Film film) throws ValidationException, ObjectNotFoundException;
    Film addLike(long filmId, long userId) throws ObjectNotFoundException;
    Film deleteLike(long filmId, long userId) throws ObjectNotFoundException;
    List<Film> getPopularFilms(int count, Map<String, String> params);
    void deleteAll();
    void delete(long id) throws ValidationException, ObjectNotFoundException;
    Mpa findMpaById(long id) throws ObjectNotFoundException;
    List<Mpa> findAllMpa();
    Genre findGenreById(long id) throws ObjectNotFoundException;
    List<Genre> findAllGenre();

    List<Film> findFilmsDirectorSort(int directorId, String sortBy);

    List<Director> findAllDirectors();

    Director findDirectorById(int directorId);

    Director addDirector(Director director);

    Director updateDirector(Director director);

    void deleteDirector(int directorId);
}
