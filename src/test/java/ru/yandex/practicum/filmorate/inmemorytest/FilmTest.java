package ru.yandex.practicum.filmorate.inmemorytest;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FeedServiceImpl;
import ru.yandex.practicum.filmorate.service.FilmServiceImpl;
import ru.yandex.practicum.filmorate.service.UserServiceImpl;
import ru.yandex.practicum.filmorate.storage.FeedDbStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.dao.DirectorDao;

import java.time.LocalDate;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class FilmTest {

    private FilmController filmController = new FilmController(
            new FilmServiceImpl(new InMemoryFilmStorage(),
                                new InMemoryUserStorage(),
                                new DirectorDao(new JdbcTemplate()),
                                new FeedDbStorage(new JdbcTemplate())));
    private UserController userController = new UserController(
            new UserServiceImpl(new InMemoryUserStorage(),
                                new FeedDbStorage(new JdbcTemplate())),
                                new FeedServiceImpl(new FeedDbStorage(new JdbcTemplate())));

    @AfterEach
    private void afterEach() {
        filmController.deleteAll();
    }
    @Test
    void createFilm() throws ValidationException {
        Film film = Film.builder()
                .id(1)
                .name("Lena")
                .description("Фильп про Лену")
                .duration(200)
                .releaseDate(LocalDate.of(2022,Month.MARCH,25))
                .build();

        filmController.create(film);
        assertEquals(1, filmController.findAll().size());
    }

    @Test
    void createFilmWithoutName() throws ValidationException {
        Film film = Film.builder()
                .id(1)
                .description("Фильп про Лену")
                .duration(200)
                .releaseDate(LocalDate.of(2022,Month.MARCH,25))
                .build();

        ValidationException ex = assertThrows(ValidationException.class, ()->filmController.create(film));
        assertEquals(ex.getMessage(), "Название фильма не может быть пустым.");
    }

    @Test
    void createFilmReleaseDate() throws ValidationException {
        Film film = Film.builder()
                .id(1)
                .description("Фильп про Лену")
                .duration(200)
                .name("Lena")
                .releaseDate(LocalDate.of(1800,Month.MARCH,25))
                .build();

        ValidationException ex = assertThrows(ValidationException.class, ()->filmController.create(film));
        assertEquals(ex.getMessage(), "Дата релиза не может быть раньше даты 28.12.1895");
    }

    @Test
    void createFilmDescription() throws ValidationException {
        Film film = Film.builder()
                .id(1)
                .description("descriptiondescriptiondescriptiondescriptiondescriptiondescriptiondescriptiondescription"
                        + "descriptiondescriptiondescriptiondescriptiondescriptiondescriptiondescriptiondescriptiondescription"
                        + "descriptiondescriptiondescriptiondescriptiondescription")
                .duration(200)
                .name("Lena")
                .releaseDate(LocalDate.of(2022,Month.MARCH,25))
                .build();

        ValidationException ex = assertThrows(ValidationException.class, ()->filmController.create(film));
        assertEquals(ex.getMessage(), "Превышена максимальная длина описания — 200 символов");
    }

    @Test
    void createFilmDuration() throws ValidationException {
        Film film = Film.builder()
                .id(1)
                .description("Фильп про Лену")
                .duration(0)
                .name("Lena")
                .releaseDate(LocalDate.of(2022,Month.MARCH,25))
                .build();

        ValidationException ex = assertThrows(ValidationException.class, ()->filmController.create(film));
        assertEquals(ex.getMessage(), "Продолжительность фильма должна быть больше 0");
    }

    @Test
    void createFilmWithoutId() throws ValidationException {
        Film film = Film.builder()
                .name("Lena")
                .description("Фильп про Лену")
                .duration(200)
                .releaseDate(LocalDate.of(2022,Month.MARCH,25))
                .build();

        filmController.create(film);
        assertEquals(1, filmController.findAll().get(0).getId());
    }

    @Test
    void updateFilm() throws ValidationException {
        Film film = Film.builder()
                .id(1)
                .name("Lena")
                .description("Фильп про Лену")
                .duration(200)
                .releaseDate(LocalDate.of(2022,Month.MARCH,25))
                .build();

        filmController.create(film);
        film.setId(2);
        ObjectNotFoundException ex = assertThrows(ObjectNotFoundException.class, ()->filmController.put(film));
        assertEquals(ex.getMessage(), "Фильм не найден.");
    }

    @Test
    void createFilmNull() throws ValidationException {
        ValidationException ex = assertThrows(ValidationException.class, ()->filmController.create(null));
        assertEquals(ex.getMessage(), "Данные о фильме не заполнены.");
        assertEquals(0, filmController.findAll().size());
    }
}
