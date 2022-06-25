package ru.yandex.practicum.filmorate.interfaces;

import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.List;

public interface UserStorage {
    List<User> findAll();
    User findById(long id) throws ObjectNotFoundException;
    User create(User user) throws ValidationException;
    User put(User user) throws ValidationException, ObjectNotFoundException;
    void deleteAll();
    User delete(User user) throws ValidationException;
}
