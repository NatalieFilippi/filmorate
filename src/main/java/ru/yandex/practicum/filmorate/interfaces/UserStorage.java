package ru.yandex.practicum.filmorate.interfaces;

import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;

public interface UserStorage {
    Collection<User> findAll();
    User findById(long id) throws ObjectNotFoundException;
    User create(User user);
    User updateUser(User user) throws  ObjectNotFoundException;
    void deleteAll();
    void deleteUser(long userId) throws ObjectNotFoundException;
    void addFriend(long userId, long friendId);
    void deleteFriend(long userId, long friendId);
    Collection<User> findFriends(long userId);

    Collection<User> findCommonFriends(long userId, long otherId);
}
