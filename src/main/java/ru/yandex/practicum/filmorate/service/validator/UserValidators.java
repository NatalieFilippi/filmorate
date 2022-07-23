package ru.yandex.practicum.filmorate.service.validator;

import org.slf4j.Logger;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.interfaces.UserStorage;

import java.time.LocalDate;

public class UserValidators {

    private static final String NO_DATA_FOUND = "Данные о пользователе не заполнены.";
    private static final String EMPTY_EMAIL = "Адрес электронной почты не может быть пустым.";
    private static final String INVALID_EMAIL = "Адрес электронной почты должен содержать символ \"@\".";
    private static final String EMPTY_LOGIN = "Логин не может быть пустым и содержать пробелы.";
    private static final String BIRTHDAY_IN_THE_FUTURE = "Дата рождения не может быть в будущем.";

    public static String check(User user) {

        String message = "";
        if (user.getEmail().isBlank()) {
            message = EMPTY_EMAIL;
        } else if (!user.getEmail().contains("@")) {
            message = INVALID_EMAIL;
        } else if (user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            message = EMPTY_LOGIN;
        } else if (user.getBirthday().isAfter(LocalDate.now())) {
            message = BIRTHDAY_IN_THE_FUTURE;
        }
        return message;
    }

    public static void isExists(UserStorage storage, long id,
                                String message, Logger log) throws ObjectNotFoundException {
        if (storage.findById(id) == null) {
            log.warn(message);
            throw new ObjectNotFoundException(message);
        }
    }
}
