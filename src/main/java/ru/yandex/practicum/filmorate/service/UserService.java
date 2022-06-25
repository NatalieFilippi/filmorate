package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.interfaces.FilmStorage;
import ru.yandex.practicum.filmorate.interfaces.UserStorage;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
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

    public List<User> findAll() {
        return userStorage.findAll();
    }

    public User findById(long id) throws ObjectNotFoundException {
        return userStorage.findById(id);
    }

    public User create(User user) throws ValidationException {
        return userStorage.create(user);
    }

    public User put(User user) throws ValidationException, ObjectNotFoundException {
        return userStorage.put(user);
    }

    public void deleteAll() {
        userStorage.deleteAll();
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
        user.addFriend(friendId);
        userFriend.addFriend(userId);
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
        user.deleteFriend(friendId);
        userFriend.deleteFriend(userId);
        return user;
    }

    public List<User> getFriends(Long userId) throws ObjectNotFoundException {
        User user = userStorage.findById(userId);
        if (user == null) {
            throw new ObjectNotFoundException(String.format("Пользователь с id %d не найден", userId));
        }
        System.out.println(user.getFriends());
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
        User user = userStorage.findById(userId);
        System.out.println(user);
        User otherUser = userStorage.findById(otherId);
        System.out.println(otherUser);
        if (user == null) {
            throw new ObjectNotFoundException(String.format("Пользователь с id %d не найден", userId));
        }
        if (otherUser == null) {
            throw new ObjectNotFoundException(String.format("Пользователь с id %d не найден", otherId));
        }
        Set<Long> userFriendsId = user.getFriends();
        if (userFriendsId == null || userFriendsId.isEmpty()) {
            return Collections.EMPTY_LIST;
        }

        Set<Long> otherFriendsId = otherUser.getFriends();
        if (otherFriendsId == null || otherFriendsId.isEmpty()) {
            return Collections.EMPTY_LIST;
        }
        Set<Long> intersect = userFriendsId.stream().filter(otherFriendsId::contains).collect(Collectors.toSet());
        Set<User> userFriends = intersect.stream().
                map(id -> {
                    try {
                        return userStorage.findById(id);
                    } catch (ObjectNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toSet());
        System.out.println(userFriends);
        System.out.println(user);
        System.out.println(otherUser);
        return new ArrayList<>(userFriends);
    }
}
