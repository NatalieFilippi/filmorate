package ru.yandex.practicum.filmorate.interfaces;

import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import javax.validation.Valid;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface FilmStorage {

    List<Film> findAll();
    Film findById(long id) throws ObjectNotFoundException;
    Film create(Film film);
    Film put(Film film) throws ObjectNotFoundException;
    void deleteAll();
    void delete(long filmId) throws ObjectNotFoundException;
    boolean addLike(long filmId, long userId);
    boolean deleteLike(long filmId, long userId);
    List<Film> getPopularFilms(int count);

    Mpa findMpaById(long id) throws ObjectNotFoundException;

    List<Mpa> findAllMpa();

    Genre findGenreById(long id) throws ObjectNotFoundException;

    List<Genre> findAllGenre();

    List<Film> findFilmsOfDirectorSortByYear(int directorId);

    List<Film> findFilmsOfDirectorSortByLikes(int directorId);

    List<Film> getUserFilms(long userId);

    Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException;

}
