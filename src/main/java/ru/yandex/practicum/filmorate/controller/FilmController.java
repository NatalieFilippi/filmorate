package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmServiceImpl;

import javax.validation.constraints.Positive;
import java.util.Collection;
import java.util.Optional;

@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {
    private final FilmServiceImpl filmService;

    @GetMapping
    public Collection<Film> findAll() {
        return filmService.findAllFilms();
    }

    @GetMapping("/{id}")
    public Film findById(@PathVariable("id") long filmId) throws ObjectNotFoundException {
        return filmService.findFilmById(filmId);
    }

    @PostMapping
    public Film create(@RequestBody Film film) throws ValidationException {
        return filmService.create(film);
    }

    @PutMapping
    public Film put(@RequestBody Film film) throws ValidationException, ObjectNotFoundException {
        return filmService.updateFilm(film);
    }

    //PUT /films/{id}/like/{userId}
    @PutMapping("/{id}/like/{userId}")
    public boolean addLike(@PathVariable("id") long filmId,
                           @PathVariable long userId) throws ObjectNotFoundException {
        return filmService.addLike(filmId, userId);
    }

    //DELETE /films/{filmId}
    @DeleteMapping("/{filmId}")
    public void deleteFilm(@PathVariable long filmId) throws ObjectNotFoundException {
        filmService.deleteFilm(filmId);
    }

    //DELETE /films/{id}/like/{userId}
    @DeleteMapping("/{id}/like/{userId}")
    public boolean deleteLike(@PathVariable("id") long filmId,
                              @PathVariable long userId) throws ObjectNotFoundException {
        return filmService.deleteLike(filmId, userId);
    }

    //todo delete
    //GET /films/popular?count={count}
    @GetMapping("/popular")
    public Collection<Film> getPopularFilms(
            @Positive @RequestParam(name="count", defaultValue = "10") int count) throws ValidationException{
        return filmService.findNMostPopularFilms(Optional.of(count));
    }

    //films/director/{directorId}?sortBy=[year,likes]
    @GetMapping("/director/{directorId}")
    public Collection<Film> findFilmsDirectorSort(@PathVariable int directorId,
                                                  @RequestParam String sortBy) throws ObjectNotFoundException {
        return filmService.findFilmsDirectorSort(directorId, sortBy);
    }

    //метод для тестов
    public void deleteAll() {
        filmService.deleteAll();
    }

}
