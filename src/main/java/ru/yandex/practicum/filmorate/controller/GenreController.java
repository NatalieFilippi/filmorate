package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.FilmServiceImpl;

import java.util.Collection;

@RestController
@RequestMapping("/genres")
@RequiredArgsConstructor
public class GenreController {
    private final FilmServiceImpl filmService;

    @GetMapping
    public Collection<Genre> findAll() {
        return filmService.findAllGenre();
    }

    @GetMapping("/{id}")
    public Genre findById(@PathVariable("id") int genreId) throws ObjectNotFoundException {
        return filmService.findGenreById(genreId);
    }
}
