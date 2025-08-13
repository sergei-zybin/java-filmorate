package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.MpaDbStorage;

import java.util.List;

@RestController
@RequestMapping
@Slf4j
@RequiredArgsConstructor
public class ReferenceController {
    private final GenreDbStorage genreDbStorage;
    private final MpaDbStorage mpaDbStorage;

    @GetMapping("/genres")
    public List<Genre> getAllGenres() {
        log.info("Запрошен список всех жанров");
        return genreDbStorage.getAll();
    }

    @GetMapping("/genres/{id}")
    public Genre getGenreById(@PathVariable int id) {
        log.info("Запрошен жанр с ID: {}", id);
        return genreDbStorage.getById(id);
    }

    @GetMapping("/mpa")
    public List<MpaRating> getAllMpaRatings() {
        log.info("Запрошен список всех рейтингов MPA");
        return mpaDbStorage.getAll();
    }

    @GetMapping("/mpa/{id}")
    public MpaRating getMpaRatingById(@PathVariable int id) {
        log.info("Запрошен рейтинг MPA с ID: {}", id);
        return mpaDbStorage.getById(id);
    }
}