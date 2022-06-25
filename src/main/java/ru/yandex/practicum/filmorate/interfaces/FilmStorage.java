package ru.yandex.practicum.filmorate.interfaces;

import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.util.List;

public interface FilmStorage {

    List<Film> findAll();
    Film findById(long id) throws ObjectNotFoundException;
    Film create(Film film) throws ValidationException;
    Film put(Film film) throws ValidationException, ObjectNotFoundException;
    void deleteAll();
    Film delete(Film film) throws ValidationException;
}
