package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class UserTest {
    private UserController userController = new UserController(
            new UserService(new InMemoryFilmStorage(), new InMemoryUserStorage()));

    @AfterEach
    private void afterEach() {
        userController.deleteAll();
    }
    @Test
    void createUser() throws ValidationException {
        User user = User.builder()
                .birthday(LocalDate.of(1980, Month.NOVEMBER,17))
                .email("name@yandex.ru")
                .login("nick")
                .name("Lena")
                .build();

        userController.create(user);
        assertEquals(1, userController.findAll().size());
    }

    @Test
    void createUserWithoutLogin() throws ValidationException {
        User user = User.builder()
                .birthday(LocalDate.of(1980, Month.NOVEMBER,17))
                .email("name@yandex.ru")
                .name("Lena")
                .build();

        ValidationException ex = assertThrows(ValidationException.class, ()->userController.create(user));
        assertEquals(ex.getMessage(), "Логин не может быть пустым и содержать пробелы.");
    }

    @Test
    void createUserLoginSpace() throws ValidationException {
        User user = User.builder()
                .birthday(LocalDate.of(1980, Month.NOVEMBER,17))
                .email("name@yandex.ru")
                .login("nick 1")
                .name("Lena")
                .build();

        ValidationException ex = assertThrows(ValidationException.class, ()->userController.create(user));
        assertEquals(ex.getMessage(), "Логин не может быть пустым и содержать пробелы.");
    }

    @Test
    void createUserWithoutId() throws ValidationException {
        User user = User.builder()
                .birthday(LocalDate.of(1980, Month.NOVEMBER,17))
                .email("name@yandex.ru")
                .login("nick")
                .name("Lena")
                .build();

        userController.create(user);
        assertEquals(1, userController.findAll().get(0).getId());
    }

    @Test
    void createUserWithoutEmail() throws ValidationException {
        User user = User.builder()
                .birthday(LocalDate.of(1980, Month.NOVEMBER,17))
                .login("nick")
                .name("Lena")
                .build();

        ValidationException ex = assertThrows(ValidationException.class, ()->userController.create(user));
        assertEquals(ex.getMessage(), "Адрес электронной почты не может быть пустым.");
    }

    @Test
    void createUserWithoutAt() throws ValidationException {
        User user = User.builder()
                .birthday(LocalDate.of(1980, Month.NOVEMBER,17))
                .login("nick")
                .email("nameyandex.ru")
                .name("Lena")
                .build();

        ValidationException ex = assertThrows(ValidationException.class, ()->userController.create(user));
        assertEquals(ex.getMessage(), "Адрес электронной почты должен содержать символ \"@\".");
    }

    @Test
    void createUserBirthday() throws ValidationException {
        User user = User.builder()
                .birthday(LocalDate.of(2500, Month.NOVEMBER,17))
                .login("nick")
                .email("name@yandex.ru")
                .name("Lena")
                .build();

        ValidationException ex = assertThrows(ValidationException.class, ()->userController.create(user));
        assertEquals(ex.getMessage(), "Дата рождения не может быть в будущем.");
    }

    @Test
    void createUserWithoutName() throws ValidationException {
        User user = User.builder()
                .birthday(LocalDate.of(1980, Month.NOVEMBER,17))
                .email("name@yandex.ru")
                .login("nick")
                .build();

        userController.create(user);
        assertEquals("nick", userController.findAll().get(0).getName());
    }

    @Test
    void updateUser() throws ValidationException, ObjectNotFoundException {
        User user = User.builder()
                .birthday(LocalDate.of(1980, Month.NOVEMBER,17))
                .email("name@yandex.ru")
                .login("nick")
                .name("Lena")
                .build();

        userController.create(user);
        user.setName("Lana");
        userController.put(user);
        assertEquals("Lana", userController.findAll().get(0).getName());
    }

    @Test
    void updateUserNonExistent() throws ValidationException {
        User user = User.builder()
                .birthday(LocalDate.of(1980, Month.NOVEMBER,17))
                .email("name@yandex.ru")
                .login("nick")
                .name("Lena")
                .build();

        userController.create(user);
        user.setId(2);
        ValidationException ex = assertThrows(ValidationException.class, ()->userController.put(user));
        assertEquals(ex.getMessage(), "Пользователь не найден.");
    }

    @Test
    void createUserID() throws ValidationException {
        User user = User.builder()
                .birthday(LocalDate.of(1980, Month.NOVEMBER,17))
                .email("name@yandex.ru")
                .login("nick")
                .name("Lena")
                .build();
        userController.create(user);

        User user1 = User.builder()
                .birthday(LocalDate.of(1990, Month.NOVEMBER,17))
                .email("name1@yandex.ru")
                .login("nick")
                .name("Lana")
                .build();
        userController.create(user1);

        User user2 = User.builder()
                .birthday(LocalDate.of(1970, Month.NOVEMBER,17))
                .email("name2@yandex.ru")
                .login("nick")
                .name("Lina")
                .build();
        userController.create(user2);
        assertEquals(3, userController.findAll().get(2).getId());
    }

    @Test
    void createUserNull() throws ValidationException {
        ValidationException ex = assertThrows(ValidationException.class, ()->userController.create(null));
        assertEquals(ex.getMessage(), "Данные о пользователе не заполнены.");
        assertEquals(0, userController.findAll().size());
    }
}
