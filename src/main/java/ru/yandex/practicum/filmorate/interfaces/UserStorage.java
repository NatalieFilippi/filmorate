package ru.yandex.practicum.filmorate.interfaces;

import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.List;

public interface UserStorage {
    public List<User> findAll();
    public User findById(long id) throws ObjectNotFoundException;
    public User create(User user) throws ValidationException;
    public User put(User user) throws ValidationException, ObjectNotFoundException;
    public void deleteAll();
    public User delete(User user) throws ValidationException;
}
