package ru.yandex.practicum.filmorate.dbtest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmDBTest {

    private final UserDbStorage userStorage;
    private final FilmDbStorage filmStorage;
    @BeforeEach
    private void beforeEach() throws ValidationException {
        User user = User.builder()
                .name("Тест")
                .email("test@ya.ru")
                .login("test")
                .birthday(LocalDate.parse("1990-05-05"))
                .build();
        userStorage.create(user);
        Film film = Film.builder()
                .name("Тест фильм")
                .description("Тест описание фильма")
                .releaseDate(LocalDate.parse("2022-07-14"))
                .mpa(new Mpa(1, "G"))
                .duration(100)
                .build();
        filmStorage.create(film);
    }

    @AfterEach
    private void afterEach() {
        userStorage.deleteAll();
        filmStorage.deleteAll();
    }


    @Test
    public void testFindFilmById() throws ObjectNotFoundException {
        List<Film> films = filmStorage.findAll();
        Long id = films.get(0).getId();
        Optional<Film> filmOptional = Optional.ofNullable(filmStorage.findById(id));

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("id", id)
                );
    }

    @Test
    public void testFindFilmByIdUnknown() {
        ObjectNotFoundException ex = assertThrows(ObjectNotFoundException.class, ()->filmStorage.findById(-1));
        assertEquals(ex.getMessage(), "Фильм не найден!");
    }

    @Test
    public void testFindAllFilm() {
        List<Film> films = filmStorage.findAll();
        assertEquals(films.size(), 1);
    }

    @Test
    public void testUpdateFilm() throws ObjectNotFoundException {
        List<Film> films = filmStorage.findAll();
        Film film = films.get(0);
        film.setName("Test update");
        filmStorage.put(film);
        Optional<Film> filmOptional = Optional.ofNullable(filmStorage.findById(film.getId()));

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(f ->
                        assertThat(f).hasFieldOrPropertyWithValue("name", "Test update")
                );
    }

    @Test
    public void testDeleteFilm() throws ObjectNotFoundException {
        List<Film> films = filmStorage.findAll();
        Film film = films.get(0);
        filmStorage.delete(film.getId());
        assertEquals(filmStorage.findAll().size(), 0);
    }

    @Test
    public void testDeleteFilmUnknown() {
        ObjectNotFoundException ex = assertThrows(ObjectNotFoundException.class, ()->filmStorage.delete(-1));
        assertEquals(ex.getMessage(), "Фильм не найден.");
    }

    @Test
    public void testAddLike() {
        Film film = filmStorage.findAll().get(0);
        User user = userStorage.findAll().get(0);
        assertEquals(filmStorage.addLike(film.getId(),user.getId()), true);
    }

    @Test
    public void testDeleteLike() {
        Film film = filmStorage.findAll().get(0);
        User user = userStorage.findAll().get(0);
        filmStorage.addLike(film.getId(),user.getId());
        assertEquals(filmStorage.deleteLike(film.getId(),user.getId()), true);
    }

    @Test
    public void testGetPopularFilms() {
        List<Film> films = filmStorage.getPopularFilms(1);
        assertEquals(films.size(), 1);
    }

    //MPA
    @Test
    public void testFindMPAById() throws ObjectNotFoundException {
        Optional<Mpa> mpaOptional = Optional.ofNullable(filmStorage.findMpaById(1));

        assertThat(mpaOptional)
                .isPresent()
                .hasValueSatisfying(mpa ->
                        assertThat(mpa).hasFieldOrPropertyWithValue("name", "G")
                );
    }

    @Test
    public void testFindMPAByIdUnknown() {
        ObjectNotFoundException ex = assertThrows(ObjectNotFoundException.class, ()->filmStorage.findMpaById(-1));
        assertEquals(ex.getMessage(), "Неизвестный рейтинг");
    }

    @Test
    public void testFindAllMpa() {
        assertEquals(filmStorage.findAllMpa().size(), 5);
    }

    //Жанры
    @Test
    public void testFindGenreById() throws ObjectNotFoundException {
        Optional<Genre> genreOptional = Optional.ofNullable(filmStorage.findGenreById(1));

        assertThat(genreOptional)
                .isPresent()
                .hasValueSatisfying(mpa ->
                        assertThat(mpa).hasFieldOrPropertyWithValue("name", "Комедия")
                );
    }

    @Test
    public void testFindGenreByIdUnknown() {
        ObjectNotFoundException ex = assertThrows(ObjectNotFoundException.class, ()->filmStorage.findGenreById(-1));
        assertEquals(ex.getMessage(), "Неизвестный жанр");
    }

    @Test
    public void testFindAllGenres() {
        assertEquals(filmStorage.findAllGenre().size(), 6);
    }

}
