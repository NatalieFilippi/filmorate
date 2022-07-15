package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.FilmServiceImpl;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/mpa")
public class MpaController {
    private final FilmServiceImpl filmService;

    @Autowired
    public MpaController(FilmServiceImpl filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public List<Mpa> findAll() {
        return filmService.findAllMpa();
    }

    @GetMapping("/{id}")
    public Mpa findById(@Valid @PathVariable("id") long id) throws ObjectNotFoundException {
        return filmService.findMpaById(id);
    }

}
