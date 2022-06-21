package ru.yandex.practicum.filmorate.interfaces;

import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.util.List;

public interface FilmStorage {

    public List<Film> findAll();
    public Film findById(long id) throws ObjectNotFoundException;
    public Film create(Film film) throws ValidationException;
    public Film put(Film film) throws ValidationException, ObjectNotFoundException;
    public void deleteAll();
    public Film delete(Film film) throws ValidationException;
}
