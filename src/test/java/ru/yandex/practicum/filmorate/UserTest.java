package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class UserTest {
    private UserController userController = new UserController();

    @Test
    void createUser() throws ValidationException {
        User user = User.builder()
                .birthday(LocalDate.of(1980, Month.NOVEMBER,17))
                .email("name@yandex.ru")
                .login("nick")
                .id(1)
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
                .id(1)
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
                .id(1)
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
        assertEquals(1, userController.findAll().stream().findFirst().get().getId());
    }

    @Test
    void createUserWithoutEmail() throws ValidationException {
        User user = User.builder()
                .birthday(LocalDate.of(1980, Month.NOVEMBER,17))
                .login("nick")
                .id(1)
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
                .id(1)
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
                .id(1)
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
                .id(1)
                .build();

        userController.create(user);
        List<User> users = userController.findAll().stream().collect(Collectors.toList());
        assertEquals("nick", users.get(0).getName());
    }

    @Test
    void updateUser() throws ValidationException {
        User user = User.builder()
                .birthday(LocalDate.of(1980, Month.NOVEMBER,17))
                .email("name@yandex.ru")
                .login("nick")
                .id(1)
                .name("Lena")
                .build();

        userController.create(user);
        user.setName("Lana");
        userController.put(user);
        List<User> users = userController.findAll().stream().collect(Collectors.toList());
        assertEquals("Lana", users.get(0).getName());
    }

    @Test
    void updateUserNonExistent() throws ValidationException {
        User user = User.builder()
                .birthday(LocalDate.of(1980, Month.NOVEMBER,17))
                .email("name@yandex.ru")
                .login("nick")
                .id(1)
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
                .id(1)
                .name("Lena")
                .build();
        userController.create(user);

        User user1 = User.builder()
                .birthday(LocalDate.of(1990, Month.NOVEMBER,17))
                .email("name1@yandex.ru")
                .login("nick")
                .id(5)
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
        List<User> users = userController.findAll().stream().collect(Collectors.toList());
        assertEquals(6, users.get(2).getId());
    }
}
