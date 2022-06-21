package ru.yandex.practicum.filmorate.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.interfaces.UserStorage;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
public class InMemoryUserStorage implements UserStorage {
    private long lastUsedId = 0;
    private final HashMap<Long, User> users = new HashMap<>();
    private final static Logger log = LoggerFactory.getLogger(UserController.class);

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User findById(long id) throws ObjectNotFoundException {

        if (!users.containsKey(id)) {
            throw new ObjectNotFoundException("Пользователь не найден.");
        }
        return users.get(id);
    }

    @Override
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

    @Override
    public User put(@Valid @RequestBody User user) throws ValidationException, ObjectNotFoundException {
        String message = check(user);
        if (!users.containsKey(user.getId())) {
            throw new ObjectNotFoundException("Пользователь не найден.");
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

    @Override
    public void deleteAll() {
        users.clear();
    }

    public User delete(User user) throws ValidationException {
        String message = check(user);
        if (message.isBlank()) {
            log.debug("Сохранён пользователь: {}", user.toString());
            return users.remove(user.getId());
        } else {
            log.debug(message);
            throw new ValidationException(message);
        }
    }


    //ДОПОЛНИТЕЛЬНЫЕ МЕТОДЫ
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

}
