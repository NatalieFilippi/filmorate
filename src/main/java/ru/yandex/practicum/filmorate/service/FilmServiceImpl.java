package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.interfaces.FeedStorage;
import ru.yandex.practicum.filmorate.interfaces.FilmService;
import ru.yandex.practicum.filmorate.interfaces.FilmStorage;
import ru.yandex.practicum.filmorate.interfaces.UserStorage;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.service.validator.DirectorValidators;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.Month;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Slf4j
@Service
public class FilmServiceImpl implements FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final FeedStorage feedStorage;
    private final DirectorStorage directorStorage;
    private final static LocalDate DATE_BORN_MOVIE = LocalDate.of(1895, Month.DECEMBER, 28);
    private static final String NO_DATA_FOUND = "Данные о фильме не заполнены.";
    private static final String EMPTY_NAME = "Название фильма не может быть пустым.";
    private static final String MAX_DESCRIPTION_LENGTH = "Превышена максимальная длина описания — 200 символов";
    private static final String DURATION_IS_POSITIVE = "Продолжительность фильма должна быть больше 0";
    private static final String EARLY_RELEASE_DATE = "Дата релиза не может быть раньше даты 28.12.1895";

    public FilmServiceImpl(@Qualifier("filmDbStorage") FilmStorage filmStorage, UserStorage userStorage, DirectorStorage directorStorage, FeedStorage feedStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.directorStorage = directorStorage;
        this.feedStorage = feedStorage;
    }

    @Override
    public List<Film> findAll() {
        return filmStorage.findAll();
    }

    @Override
    public Film findById(long id) throws ObjectNotFoundException {
        return filmStorage.findById(id);
    }

    @Override
    public Film create(Film film) throws ValidationException {
        String message = check(film);
        if (!message.isBlank()) {
            log.debug("Ошибка при попытке добавления нового фильма: " + message);
            throw new ValidationException(message);
        }
        log.debug(String.format("Добавлен новый фильм %d.", film.getId()));
        return filmStorage.create(film);
    }

    @Override
    public Film put(Film film) throws ValidationException, ObjectNotFoundException {
        String message = check(film);
        if (!message.isBlank()) {
            log.debug("Ошибка при попытке редактирования фильма: " + message);
            throw new ValidationException(message);
        }
        log.debug(String.format("Изменения для фильма %d успешно приняты.", film.getId()));
        return filmStorage.put(film);
    }

    @Override
    public Film addLike(long filmId, long userId) throws ObjectNotFoundException {
        Film film = filmStorage.findById(filmId);
        User user = userStorage.findById(userId);
        if (film == null) {
            log.debug(String.format("Ошибка при попытке лайкнуть фильм. Фильм %d не найден.",filmId));
            throw new ObjectNotFoundException(String.format("Фильм с id %d не найден", filmId));
        }
        if (user == null) {
            log.debug(String.format("Ошибка при попытке лайкнуть фильм. Пользователь %d не найден.",userId));
            throw new ObjectNotFoundException(String.format("Пользователь с id %d не найден", userId));
        }
        if (filmStorage.addLike(filmId, userId)) {
            log.debug(String.format("Пользователь %d лайкнул фильм %d",
                                    user.getId(), film.getId()));
            feedStorage.addEvent(Event.builder()
                    .userId(userId)
                    .eventType("LIKE")
                    .operation("ADD")
                    .timestamp(new Timestamp(System.currentTimeMillis()).getTime())
                    .entityId(filmId)
                    .build());
        };
        return film;
    }

    @Override
    public Film deleteLike(long filmId, long userId) throws ObjectNotFoundException {
        Film film = filmStorage.findById(filmId);
        User user = userStorage.findById(userId);
        if (film == null) {
            log.debug(String.format("Ошибка при попытке лайкнуть фильм. Фильм %d не найден.",filmId));
            throw new ObjectNotFoundException(String.format("Фильм с id %d не найден", filmId));
        }
        if (user == null) {
            log.debug(String.format("Ошибка при попытке лайкнуть фильм. Пользователь %d не найден.",userId));
            throw new ObjectNotFoundException(String.format("Пользователь с id %d не найден", userId));
        }
        filmStorage.deleteLike(filmId, userId);
        log.debug(String.format("Пользователь %d удалил лайк у фильма %d",
                                user.getId(), film.getId()));
        feedStorage.addEvent(Event.builder()
                .userId(userId)
                .eventType("LIKE")
                .operation("REMOVE")
                .timestamp(new Timestamp(System.currentTimeMillis()).getTime())
                .entityId(filmId)
                .build());
        return film;
    }

    @Override
    public List<Film> getPopularFilms(int count, Map<String, String> params) {
        return filmStorage.getPopularFilms(count, params);
    }

    @Override
    public List<Film> search(String query, List<String> searchOptions) {
        if (searchOptions.size() > 2) {
            throw new ValidationException("Превышено количество задаваемых параметров поиска!");
        }

        if (searchOptions.size() > new HashSet<>(searchOptions).size()) {
            throw new ValidationException("В строке запроса есть повторяющиеся опции поиска!");
        }

        return filmStorage.search(query, searchOptions);
    }

    @Override
    public void deleteAll() {
        log.debug("Все фильмы удалены из системы. :(");
        filmStorage.deleteAll();
    }

    @Override
    public void delete(long id) throws ValidationException, ObjectNotFoundException {
        filmStorage.delete(id);
    }

    @Override
    public Mpa findMpaById(long id) throws ObjectNotFoundException {
        return filmStorage.findMpaById(id);
    }

    @Override
    public List<Mpa> findAllMpa() {
        return filmStorage.findAllMpa();
    }

    @Override
    public Genre findGenreById(long id) throws ObjectNotFoundException {
        return filmStorage.findGenreById(id);
    }

    @Override
    public List<Genre> findAllGenre() {
        return filmStorage.findAllGenre();
    }

    public List<Director> findAllDirectors() {
        return directorStorage.findAll();
    }

    public Director findDirectorById(int directorId) throws ObjectNotFoundException {
        DirectorValidators.isDirectorExists(directorStorage, directorId, String.format(
                "Режиссёр с id = %s не существует.", directorId), log);
        return directorStorage.find(directorId);
    }

    public List<Film> findFilmsDirectorSort(int directorId, String sortBy) throws ObjectNotFoundException {
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

    private String check(Film film) throws ValidationException {
        String message = "";
        if (film == null) {
            message = NO_DATA_FOUND;
        } else if (film.getName() == null || film.getName().isBlank()) {
            message = EMPTY_NAME;
        } else if (film.getDescription().length() > 200) {
            message = MAX_DESCRIPTION_LENGTH;
        } else if (film.getReleaseDate().isBefore(DATE_BORN_MOVIE)) {
            message = EARLY_RELEASE_DATE;
        } else if (film.getDuration() <= 0) {
            message = DURATION_IS_POSITIVE;
        }
        return message;
    }

    public List <Film> findCommonFilms(long userId, long friendId) throws ObjectNotFoundException {
        User user = userStorage.findById(userId);
        User otherUser = userStorage.findById(friendId);
        if (user == null) {
            log.debug(String.format("Ошибка при попытке найти общих друзей. Пользователь с id %d не найден", userId));
            throw new ObjectNotFoundException(String.format("Пользователь с id %d не найден", userId));
        }
        if (otherUser == null) {
            log.debug(String.format("Ошибка при попытке найти общих друзей. Пользователь с id %d не найден", friendId));
            throw new ObjectNotFoundException(String.format("Пользователь с id %d не найден", friendId));
        }
        return (filmStorage.getUserFilms(userId)
                .stream()
                .filter(filmStorage.getUserFilms(friendId)::contains)
                .collect(Collectors.toList()));
    }
}
