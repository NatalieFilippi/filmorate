package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.interfaces.FilmStorage;
import ru.yandex.practicum.filmorate.interfaces.UserStorage;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.validator.UserValidators;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl {
    private final UserStorage userStorage;

    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    public User findById(long id) throws ObjectNotFoundException {
        UserValidators.isExists(userStorage, id, String.format(
                "Пользователь с id = %s не существует.", id), log);
        return userStorage.findById(id);
    }

    public User addUser(User user) throws ValidationException {
        String message = UserValidators.check(user);
        if (!message.isBlank()) {
            log.debug("Ошибка при попытке добавления пользователя: " + message);
            throw new ValidationException(message);
        }
        return userStorage.create(user);
    }

    public User updateUser(User user) throws ValidationException, ObjectNotFoundException {
        String message = UserValidators.check(user);
        if (!message.isBlank()) {
            log.debug("Ошибка при попытке редактирования пользователя: " + message);
            throw new ValidationException(message);
        }

        UserValidators.isExists(userStorage, user.getId(), String.format(
                "Пользователь с id = %s не существует.", user.getId()), log);

        return userStorage.updateUser(user);
    }

    public void deleteAll() {
        log.debug("Все пользователи удалены из системы. :(");
        userStorage.deleteAll();
    }

    public void deleteUser(long id) throws ObjectNotFoundException {
        UserValidators.isExists(userStorage, id, "Невалидный id пользователя, ", log);
        userStorage.deleteUser(id);
    }

    public void addFriend(Long userId, Long friendId) throws ObjectNotFoundException {
        // Проверить существование обоих User
        UserValidators.isExists(userStorage, userId, "Невалидный id пользователя, " +
                "направившего заявку на добавление в друзья.", log);
        UserValidators.isExists(userStorage, friendId, String.format(
                "Ошибка при добавлении в друзья. Пользователь с id = %s не существует.", friendId), log);

        userStorage.addFriend(userId, friendId);
    }

    public void deleteFriend(Long userId, Long friendId) throws ObjectNotFoundException {
        // Проверить существование обоих User
        UserValidators.isExists(userStorage, userId,
                "Невалидный id пользователя, направившего заявку " +
                        "на удаление из друзей.", log);
        UserValidators.isExists(userStorage, friendId, String.format(
                "Ошибка при удалении. Пользователь с id = %s не существует.", friendId), log);

        userStorage.deleteFriend(userId, friendId);

    }

    public Collection<User> findUserFriends(long userId) throws ObjectNotFoundException {
        // Проверить существование User
        UserValidators.isExists(userStorage, userId, "Пользователь с id = %s не существует.", log);

        return userStorage.findFriends(userId);
    }

    public Collection<User> findCommonFriends(Long userId, Long otherId) throws ObjectNotFoundException {
        // Проверить существование обоих User
        UserValidators.isExists(userStorage, userId, String.format(
                "Пользователь с id = %s не существует.", otherId), log);
        UserValidators.isExists(userStorage, otherId, String.format(
                "Пользователь с id = %s не существует.", otherId), log);

        return userStorage.findCommonFriends(userId,otherId);
    }
}
