package ru.yandex.practicum.filmorate;

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
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmorateApplicationTests {

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


	//ПОЛЬЗОВАТЕЛИ
	@Test
	public void testFindUserById() throws ObjectNotFoundException {
		List<User> users = userStorage.findAll();
		Long id = users.get(0).getId();
		Optional<User> userOptional = Optional.ofNullable(userStorage.findById(id));

		assertThat(userOptional)
				.isPresent()
				.hasValueSatisfying(user ->
						assertThat(user).hasFieldOrPropertyWithValue("id", id)
				);
	}

	@Test
	public void testFindUserByIdUnknown() throws ObjectNotFoundException {
		ObjectNotFoundException ex = assertThrows(ObjectNotFoundException.class, ()->userStorage.findById(-1));
		assertEquals(ex.getMessage(), "Пользователь не найден!");
	}

	@Test
	public void testFindAllUser() throws ObjectNotFoundException {
		List<User> users = userStorage.findAll();
		assertEquals(users.size(), 1);
	}

	@Test
	public void testUpdateUser() throws ObjectNotFoundException, ValidationException {
		List<User> users = userStorage.findAll();
		User user = users.get(0);
		user.setName("Test update");
		userStorage.put(user);
		Optional<User> userOptional = Optional.ofNullable(userStorage.findById(user.getId()));

		assertThat(userOptional)
				.isPresent()
				.hasValueSatisfying(u ->
						assertThat(u).hasFieldOrPropertyWithValue("name", "Test update")
				);
	}


		//ФИЛЬМЫ
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
	public void testFindFilmByIdUnknown() throws ObjectNotFoundException {
		ObjectNotFoundException ex = assertThrows(ObjectNotFoundException.class, ()->filmStorage.findById(-1));
		assertEquals(ex.getMessage(), "Фильм не найден!");
	}


	@Test
	public void testFindAllFilm() throws ObjectNotFoundException {
		List<Film> films = filmStorage.findAll();
		assertEquals(films.size(), 1);
	}

	@Test
	public void testUpdateFilm() throws ObjectNotFoundException, ValidationException {
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
	void contextLoads() {
	}

}
