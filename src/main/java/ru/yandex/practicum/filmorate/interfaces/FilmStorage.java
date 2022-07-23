package ru.yandex.practicum.filmorate.interfaces;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Optional;

public interface FilmStorage {

    Collection<Film> findAllFilms();
    Film findFilmById(long id);
    Film create(Film film);
    Film updateFilm(Film film);
    void deleteAll();
    void deleteFilm(long filmId);
    boolean addLike(long filmId, long userId);
    boolean deleteLike(long filmId, long userId);
    Collection<Film> findNMostPopularFilms(Optional<Integer> count);

    Collection<Film> findFilmsOfDirectorSortByYear(int directorId);

    Collection<Film> findFilmsOfDirectorSortByLikes(int directorId);

}
