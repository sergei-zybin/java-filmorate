package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.storage.MpaRatingDao;
import ru.yandex.practicum.filmorate.storage.GenreDao;
import java.util.*;

@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final MpaRatingDao mpaRatingDao;
    private final GenreDao genreDao;

    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage,
                       @Qualifier("userDbStorage") UserStorage userStorage,
                       MpaRatingDao mpaRatingDao,
                       GenreDao genreDao) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.mpaRatingDao = mpaRatingDao;
        this.genreDao = genreDao;
    }

    public Film create(Film film) {
        validateMpaAndGenres(film);
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        validateMpaAndGenres(film);
        return filmStorage.update(film);
    }

    public List<Film> getAll() {
        return filmStorage.getAll();
    }

    public Film getFilmById(int id) {
        return filmStorage.getFilmById(id);
    }

    public void addLike(int filmId, int userId) {
        filmStorage.getFilmById(filmId);
        userStorage.getUserById(userId);
        filmStorage.addLike(filmId, userId);
    }

    public void removeLike(int filmId, int userId) {
        filmStorage.getFilmById(filmId);
        userStorage.getUserById(userId);
        filmStorage.removeLike(filmId, userId);
    }

    public List<Film> getPopularFilms(int count) {
        return filmStorage.getPopularFilms(count);
    }

    private void validateMpaAndGenres(Film film) {
        if (film.getMpa() != null && film.getMpa().getId() != 0) {
            mpaRatingDao.getById(film.getMpa().getId());
        }
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            for (Genre genre : film.getGenres()) {
                genreDao.getById(genre.getId());
            }
        }
    }
}