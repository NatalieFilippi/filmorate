package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.interfaces.FilmStorage;
import ru.yandex.practicum.filmorate.interfaces.UserStorage;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final static Logger log = LoggerFactory.getLogger(UserController.class);
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    @Autowired
    public UserService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public List<User> findAll() {
        return userStorage.findAll();
    }

    public User findById(long id) throws ObjectNotFoundException {
        return userStorage.findById(id);
    }

    public User create(User user) throws ValidationException {
        String message = check(user);
        if (!message.isBlank()) {
            log.debug(message);
            throw new ValidationException(message);
        }
        return userStorage.create(user);
    }

    public User put(User user) throws ValidationException, ObjectNotFoundException {
        String message = check(user);
        if (!message.isBlank()) {
            log.debug(message);
            throw new ValidationException(message);
        }
        return userStorage.put(user);
    }

    public void deleteAll() {
        userStorage.deleteAll();
    }

    public void delete(User user) throws ValidationException, ObjectNotFoundException {
        String message = check(user);
        if (!message.isBlank()) {
            log.debug(message);
            throw new ValidationException(message);
        }
        userStorage.delete(user);
    }

    public User addFriend(Long userId, Long friendId) throws ObjectNotFoundException {
        User user = userStorage.findById(userId);
        User userFriend = userStorage.findById(friendId);
        if (user == null) {
            throw new ObjectNotFoundException(String.format("Пользователь с id %d не найден", userId));
        }
        if (userFriend == null) {
            throw new ObjectNotFoundException(String.format("Пользователь с id %d не найден", friendId));
        }
        userStorage.addFriend(userId, friendId);
        return user;
    }

    public User deleteFriend(Long userId, Long friendId) throws ObjectNotFoundException {
        User user = userStorage.findById(userId);
        User userFriend = userStorage.findById(friendId);
        if (user == null) {
            throw new ObjectNotFoundException(String.format("Пользователь с id %d не найден", userId));
        }
        if (userFriend == null) {
            throw new ObjectNotFoundException(String.format("Пользователь с id %d не найден", friendId));
        }
        if (userStorage.deleteFriend(userId, friendId)) {
            return user;
        } else {
            return null;
        }

    }

    public List<User> getFriends(Long userId) throws ObjectNotFoundException {
        User user = userStorage.findById(userId);
        if (user == null) {
            throw new ObjectNotFoundException(String.format("Пользователь с id %d не найден", userId));
        }
        return userStorage.getFriends(userId);
    }

    public List<User> getCommonFriends(Long userId, Long otherId) throws ObjectNotFoundException {
        User user = userStorage.findById(userId);
        User otherUser = userStorage.findById(otherId);
        if (user == null) {
            throw new ObjectNotFoundException(String.format("Пользователь с id %d не найден", userId));
        }
        if (otherUser == null) {
            throw new ObjectNotFoundException(String.format("Пользователь с id %d не найден", otherId));
        }

        return userStorage.getCommonFriends(userId,otherId);
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
}
