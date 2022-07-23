package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.FilmServiceImpl;

import java.util.Collection;

@RestController
@RequestMapping("/mpa")
@RequiredArgsConstructor

public class MpaController {
    private final FilmServiceImpl filmService;

    @GetMapping
    public Collection<Mpa> findAll() {
        return filmService.findAllMpa();
    }

    @GetMapping("/{id}")
    public Mpa findById(@PathVariable("id") int mpaId) throws ObjectNotFoundException {
        return filmService.findMpaById(mpaId);
    }

}
