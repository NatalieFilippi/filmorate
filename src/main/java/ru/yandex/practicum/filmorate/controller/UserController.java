package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private long lastUsedId = 0;
    private final HashMap<Long, User> users = new HashMap<>();
    private final static Logger log = LoggerFactory.getLogger(UserController.class);

    @GetMapping
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) throws ValidationException {
        String message = check(user);
        if (message.isBlank()) {
            user.setId(getNextId());
            users.put(user.getId(), user);
        } else {
            log.debug(message);
            throw new ValidationException(message);
        }
        log.debug("Сохранён пользователь: {}", user.toString());
        return user;
    }

    @PutMapping
    public User put(@Valid @RequestBody User user) throws ValidationException {
        String message = check(user);
        if (!users.containsKey(user.getId())) {
            throw new ValidationException("Пользователь не найден.");
        }
        if (message.isBlank()) {
            users.put(user.getId(), user);
        } else {
            log.debug(message);
            throw new ValidationException(message);
        }
        log.debug("Обновлён пользователь: {}", user.toString());
        return user;
    }

    private String check(User user) throws ValidationException {
        String message = "";
        if (user == null) {
            message = "Данные о пользователе не заполнены.";
        } else if (user.getEmail() == null || user.getEmail().isBlank()) {
            message = "Адрес электронной почты не может быть пустым.";
        } else if (!user.getEmail().contains("@")) {
            message = "Адрес электронной почты должен содержать символ \"@\".";
        } else if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            message = "Логин не может быть пустым и содержать пробелы.";
        } else if (user.getBirthday().isAfter(LocalDate.now())) {
            message = "Дата рождения не может быть в будущем.";
        } else if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        return message;
    }

    private long getNextId() {
        return ++lastUsedId;
    }

    //метод для тестов
    public void deleteAll() {
        users.clear();
    }

}
