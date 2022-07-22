package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.relational.core.sql.In;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.interfaces.FilmService;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public List<Film> findAll() {
        return filmService.findAll();
    }

    @GetMapping("/{id}")
    public Film findById(@Valid @PathVariable("id") long id) throws ObjectNotFoundException {
        return filmService.findById(id);
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) throws ValidationException {
       return filmService.create(film);
    }

    @PutMapping
    public Film put(@Valid @RequestBody Film film) throws ValidationException, ObjectNotFoundException {
        return filmService.put(film);
    }

    @PutMapping("/{id}/like/{userId}")
    public Film addLike(@PathVariable("id") long id, @PathVariable("userId") long userId) throws ObjectNotFoundException {
        return filmService.addLike(id,userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public Film deleteLike(@PathVariable("id") long id, @PathVariable("userId") long userId) throws ObjectNotFoundException {
        return filmService.deleteLike(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(
            @RequestParam(defaultValue = "10", required = false) Integer count,
            @RequestParam Map<String, String> params)
            throws ValidationException {

        if (count <= 0) {
            throw new ValidationException("Значение параметра count не может быть отрицательно!");
        }

        return filmService.getPopularFilms(count, params);
    }

    //метод для тестов
    public void deleteAll() {
        filmService.deleteAll();
    }

}
