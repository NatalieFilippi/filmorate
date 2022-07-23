package ru.yandex.practicum.filmorate.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.interfaces.UserStorage;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component("inMemoryUserStorage")
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
    public User create(@Valid @RequestBody User user)  {
        user.setId(getNextId());
        users.put(user.getId(), user);
        log.debug("Сохранён пользователь: {}", user.toString());
        return user;
    }

    @Override
    public User updateUser(User user) throws ObjectNotFoundException {
        if (!users.containsKey(user.getId())) {
            throw new ObjectNotFoundException("Пользователь не найден.");
        }
        users.put(user.getId(), user);
        log.debug("Обновлён пользователь: {}", user.toString());
        return user;
    }

    @Override
    public void deleteAll() {
        users.clear();
    }

    public void deleteUser(long id) {
        log.debug("Удалён пользователь: {}", id);
        users.remove(id);
    }

    @Override
    public void addFriend(long userId, long friendId) {

    }

    @Override
    public void deleteFriend(long userId, long friendId) {
    }

    @Override
    public List<User> findFriends(long userId) {
        return null;
    }

    @Override
    public List<User> findCommonFriends(long userId, long otherId) {
        return null;
    }


    //ДОПОЛНИТЕЛЬНЫЕ МЕТОДЫ


    private long getNextId() {
        return ++lastUsedId;
    }

}
