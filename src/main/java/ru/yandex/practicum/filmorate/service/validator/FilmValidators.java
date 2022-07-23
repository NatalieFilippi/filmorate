package ru.yandex.practicum.filmorate.service.validator;

import org.slf4j.Logger;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.interfaces.FilmStorage;
import ru.yandex.practicum.filmorate.storage.dao.GenreDao;
import ru.yandex.practicum.filmorate.storage.dao.MpaDao;

import java.time.LocalDate;
import java.time.Month;

public class FilmValidators {
    private final static LocalDate DATE_BORN_MOVIE = LocalDate.of(1895, Month.DECEMBER, 28);
    private static final String EMPTY_NAME = "Название фильма не может быть пустым.";
    private static final String MAX_DESCRIPTION_LENGTH = "Превышена максимальная длина описания — 200 символов";
    private static final String DURATION_IS_POSITIVE = "Продолжительность фильма должна быть больше 0";
    private static final String EARLY_RELEASE_DATE = "Дата релиза не может быть раньше даты 28.12.1895";


    public static String check(Film film) {
        String message = "";
        if (film.getName().isBlank()) {
            message = EMPTY_NAME;
        } else if (film.getDescription().length() > 200) {
            message = MAX_DESCRIPTION_LENGTH;
        } else if (film.getReleaseDate().isBefore(DATE_BORN_MOVIE)) {
            message = EARLY_RELEASE_DATE;
        } else if (film.getDuration() < 0) {
            message = DURATION_IS_POSITIVE;
        }
        return message;
    }

    public static void isExists(FilmStorage storage, long id,
                                String message, Logger log) throws ObjectNotFoundException {
        if (storage.findFilmById(id) == null) {
            log.warn(message);
            throw new ObjectNotFoundException(message);
        }
    }

    public static void isGenreExists(GenreDao storage, Integer id,
                                     String message, Logger log) throws ObjectNotFoundException {
        if (storage.findGenre(id) == null) {
            log.warn(message);
            throw new ObjectNotFoundException(message);
        }
    }

    public static void isMpaExists(MpaDao storage, Integer id,
                                   String message, Logger log) throws ObjectNotFoundException {
        if (storage.findMpaById(id) == null) {
            log.warn(message);
            throw new ObjectNotFoundException(message);
        }
    }
}