package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.interfaces.FilmStorage;
import ru.yandex.practicum.filmorate.interfaces.UserStorage;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.service.validator.DirectorValidators;
import ru.yandex.practicum.filmorate.service.validator.FilmValidators;
import ru.yandex.practicum.filmorate.service.validator.UserValidators;
import ru.yandex.practicum.filmorate.storage.dao.DirectorDao;
import ru.yandex.practicum.filmorate.storage.dao.GenreDao;
import ru.yandex.practicum.filmorate.storage.dao.MpaDao;

import java.util.Collection;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmServiceImpl {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final DirectorDao directorStorage;
    private final GenreDao genreStorage;
    private final MpaDao mpaStorage;


    public Collection<Film> findAllFilms() {
        return filmStorage.findAllFilms();
    }

    public Film findFilmById(long id) throws ObjectNotFoundException {
        FilmValidators.isExists(filmStorage, id, String.format(
                "Фильм с id = %s не существует.", id), log);
        return filmStorage.findFilmById(id);
    }

    public Film create(Film film) throws ValidationException {
        String message = FilmValidators.check(film);
        if (!message.isBlank()) {
            log.debug("Ошибка при попытке добавления нового фильма: " + message);
            throw new ValidationException(message);
        }
        return filmStorage.create(film);
    }

    public Film updateFilm(Film film) throws ValidationException, ObjectNotFoundException {
        String message = FilmValidators.check(film);
        if (!message.isBlank()) {
            log.debug("Ошибка при попытке редактирования фильма: " + message);
            throw new ValidationException(message);
        }

        FilmValidators.isExists(filmStorage, film.getId(), String.format(
                "Фильм с id = %s не существует.", film.getId()), log);
        return filmStorage.updateFilm(film);
    }

    public boolean addLike(long filmId, long userId) throws ObjectNotFoundException {
        FilmValidators.isExists(filmStorage, filmId,
                String.format("Фильм с id = %s не существует.", filmId), log);
        UserValidators.isExists(userStorage, userId, String.format(
                "Пользователь с id = %s не существует.", userId), log);

        return filmStorage.addLike(filmId, userId);
    }

    public boolean deleteLike(long filmId, long userId) throws ObjectNotFoundException {
        FilmValidators.isExists(filmStorage, filmId, String.format(
                "Фильм с id = %s не существует.", filmId), log);
        UserValidators.isExists(userStorage, userId, String.format(
                "Пользователь с id = %s не существует.", userId), log);

        return filmStorage.deleteLike(filmId, userId);
    }

    public Collection<Film> findNMostPopularFilms(Optional<Integer> count) {
        return filmStorage.findNMostPopularFilms(count);
    }

    public void deleteAll() {
        log.debug("Все фильмы удалены из системы. :(");
        filmStorage.deleteAll();
    }

    public void deleteFilm(long id) throws ObjectNotFoundException {
        FilmValidators.isExists(filmStorage, id, String.format(
                "Фильм с id = %s не существует.", id), log);
        filmStorage.deleteFilm(id);
    }

    public Mpa findMpaById(int id) throws ObjectNotFoundException {
        FilmValidators.isMpaExists(mpaStorage, id, String.format(
                "Рейтинга MPA с id = %s не существует.", id), log);
        return mpaStorage.findMpaById(id);
    }

    public Collection<Mpa> findAllMpa() {
        return mpaStorage.findAllMpa();
    }

    public Genre findGenreById(int id) throws ObjectNotFoundException {
        FilmValidators.isGenreExists(genreStorage, id, String.format(
                "Жанр фильма с id = %s не существует.", id), log);
        return genreStorage.findGenre(id);
    }

    public Collection<Genre> findAllGenre() {
        return genreStorage.findAllGenres();
    }

    public Collection<Director> findAllDirectors() {
        return directorStorage.findAll();
    }

    public Director findDirectorById(int directorId) throws ObjectNotFoundException {
        DirectorValidators.isDirectorExists(directorStorage, directorId, String.format(
                "Режиссёр с id = %s не существует.", directorId), log);
        return directorStorage.find(directorId);
    }

    public Collection<Film> findFilmsDirectorSort(int directorId, String sortBy) throws ObjectNotFoundException {
        DirectorValidators.isDirectorExists(directorStorage, directorId, String.format(
                "Режиссёр с id = %s не существует.", directorId), log);
        if (sortBy.equals("likes")) {
            return filmStorage.findFilmsOfDirectorSortByLikes(directorId);
        } else if (sortBy.equals("year")) {
            return filmStorage.findFilmsOfDirectorSortByYear(directorId);
        }
        return null;
    }

    public Director addDirector(Director director) throws ValidationException {
        String message = DirectorValidators.check(director);
        if (!message.isBlank()) {
            log.debug("Ошибка при попытке добавления нового режиссёра: " + message);
            throw new ValidationException(message);
        }
        return directorStorage.add(director);
    }

    public Director updateDirector(Director director) throws ValidationException, ObjectNotFoundException {
        String message = DirectorValidators.check(director);
        if (!message.isBlank()) {
            log.debug("Ошибка при попытке изменении режиссёра: " + message);
            throw new ValidationException(message);
        }

        DirectorValidators.isDirectorExists(directorStorage, director.getId(), String.format(
                "Режиссёр с id = %s не существует.", director.getId()), log);
        return directorStorage.update(director);
    }

    public void deleteDirector(int directorId) throws ObjectNotFoundException {
        DirectorValidators.isDirectorExists(directorStorage, directorId, String.format(
                "Режиссёр с id = %s не существует.", directorId), log);
        directorStorage.deleteFromFilm(directorId);
        directorStorage.delete(directorId);
    }
}
