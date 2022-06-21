package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.interfaces.FilmStorage;
import ru.yandex.practicum.filmorate.interfaces.UserStorage;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    @Autowired
    public UserService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public User addFriend(Long userId, Long friendId) throws ObjectNotFoundException {
        if (userStorage.findById(userId) == null) {
            throw new ObjectNotFoundException(String.format("Пользователь с id %d не найден", userId));
        }
        if (userStorage.findById(friendId) == null) {
            throw new ObjectNotFoundException(String.format("Пользователь с id %d не найден", friendId));
        }
        userStorage.findById(userId).addFriend(friendId);
        userStorage.findById(friendId).addFriend(userId);
        return userStorage.findById(userId);
    }

    public User deleteFriend(Long userId, Long friendId) throws ObjectNotFoundException {
        if (userStorage.findById(userId) == null) {
            throw new ObjectNotFoundException(String.format("Пользователь с id %d не найден", userId));
        }
        if (userStorage.findById(friendId) == null) {
            throw new ObjectNotFoundException(String.format("Пользователь с id %d не найден", friendId));
        }
        userStorage.findById(userId).deleteFriend(friendId);
        userStorage.findById(friendId).deleteFriend(userId);
        return userStorage.findById(userId);
    }

    public List<User> getFriends(Long userId) throws ObjectNotFoundException {
        if (userStorage.findById(userId) == null) {
            throw new ObjectNotFoundException(String.format("Пользователь с id %d не найден", userId));
        }
        return userStorage.findById(userId).getFriends().stream().
                map(id -> {
                    try {
                        return userStorage.findById(id);
                    } catch (ObjectNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriends(Long userId, Long otherId) throws ObjectNotFoundException {
        if (userStorage.findById(userId) == null) {
            throw new ObjectNotFoundException(String.format("Пользователь с id %d не найден", userId));
        }
        if (userStorage.findById(otherId) == null) {
            throw new ObjectNotFoundException(String.format("Пользователь с id %d не найден", otherId));
        }
        Set<Long> userFriendsId = userStorage.findById(userId).getFriends();
        if (userFriendsId == null || userFriendsId.isEmpty()) {
            return Collections.EMPTY_LIST;
        }

        Set<Long> otherFriendsId = userStorage.findById(otherId).getFriends();
        if (otherFriendsId == null || otherFriendsId.isEmpty()) {
            return Collections.EMPTY_LIST;
        }

        Set<User> userFriends = userFriendsId.stream().
                map(id -> {
                    try {
                        return userStorage.findById(id);
                    } catch (ObjectNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toSet());
        Set<User> otherFriends = otherFriendsId.stream().
                map(id -> {
                    try {
                        return userStorage.findById(id);
                    } catch (ObjectNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toSet());
        userFriends.retainAll(otherFriends);
        return new ArrayList<>(userFriends);
    }
}
