package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.interfaces.FilmStorage;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
@Primary
@Component
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Film> findAll() {
        final String sqlQuery = "SELECT * FROM FILMS LEFT JOIN MPA M ON FILMS.MPA_ID = M.MPA_ID";
        final List<Film> films = jdbcTemplate.query(sqlQuery, FilmDbStorage::makeFilm);
        if (films.size() == 0) {
            return Collections.emptyList();
        }
        for (Film film : films) {
            setGenre(film);
        }
        return films;
    }

    @Override
    public Film findById(long id) throws ObjectNotFoundException {
        final String sqlQuery = "SELECT * FROM FILMS LEFT JOIN MPA M ON FILMS.MPA_ID = M.MPA_ID WHERE FILM_ID = ?";
        final List<Film> films = jdbcTemplate.query(sqlQuery, FilmDbStorage::makeFilm, id);
        if (films.size() == 0) {
            log.debug(String.format("Фильм %d не найден.", id));
            throw new ObjectNotFoundException("Фильм не найден!");
        }
        Film film = films.get(0);
        setGenre(film);
        return film;
    }

    @Override
    public Film create(Film film) {
        final String sqlQuery = "INSERT INTO FILMS(FILM_NAME, DESCRIPTION, DURATION, MPA_ID, RELEASE_DATE) " +
                "VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"FILM_ID"});
            stmt.setString(1, film.getName());

            final String description = film.getDescription();
            if (description.isBlank()) {
                stmt.setNull(2, Types.VARCHAR);
            } else {
                stmt.setString(2, description);
            }

            stmt.setInt(3, film.getDuration());

            final int mpa = film.getMpa().getId();
            if (mpa == 0) {
                stmt.setNull(4, Types.INTEGER);
            } else {
                stmt.setInt(4, mpa);
            }

            final LocalDate releaseDate = film.getReleaseDate();
            if (releaseDate == null) {
                stmt.setNull(5, Types.DATE);
            } else {
                stmt.setDate(5, Date.valueOf(releaseDate));
            }
            return stmt;
        }, keyHolder);
        film.setId(keyHolder.getKey().longValue());

        //Обновить таблицу с жанрами
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            for (Genre g : film.getGenres()) {
                String sqlQueryGenre = "INSERT INTO FILM_GENRES(FILM_ID, GENRE_ID) " + "VALUES (?, ?)";
                jdbcTemplate.update(sqlQueryGenre, film.getId(), g.getId());
            }
        }

        return film;
    }

    @Override
    public Film put(Film film) throws ObjectNotFoundException {
        String sqlQuery = "UPDATE FILMS SET " +
                "FILM_NAME = ?, DESCRIPTION = ?, DURATION = ?, MPA_ID = ? , RELEASE_DATE = ?" +
                "WHERE FILM_ID = ?";
        int row = jdbcTemplate.update(sqlQuery
                , film.getName()
                , film.getDescription()
                , film.getDuration()
                , film.getMpa().getId()
                , film.getReleaseDate()
                , film.getId());
        if (row == 0) {
            log.debug(String.format("Фильм %d не найден.", film.getId()));
            throw new ObjectNotFoundException("Фильм не найден");
        }
        //Обновить таблицу с жанрами
        sqlQuery = "DELETE FROM FILM_GENRES WHERE FILM_ID = ?";
        jdbcTemplate.update(sqlQuery, film.getId());
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            for (Genre g : film.getGenres()) {
                sqlQuery = "MERGE INTO FILM_GENRES(FILM_ID, GENRE_ID) " + "VALUES (?, ?)";
                jdbcTemplate.update(sqlQuery, film.getId(), g.getId());
            }
            setGenre(film);
        }

        return film;
    }

    @Override
    public void deleteAll() {
        String sqlQuery = "DELETE FROM FILMS";
        jdbcTemplate.update(sqlQuery);
    }

    @Override
    public Film delete(Film film) throws ObjectNotFoundException {
        String sqlQuery = "DELETE FROM FILMS WHERE FILM_ID = ?";
        if (jdbcTemplate.update(sqlQuery, film.getId()) > 0) {
            return film;
        } else {
            log.debug(String.format("Фильм %d не найден.", film.getId()));
            throw new ObjectNotFoundException("Фильм не найден.");
        }
    }

    @Override
    public boolean addLike(long filmId, long userId) {
        final String sqlQuery = "INSERT INTO LIKES(USER_ID, FILM_ID) " + "VALUES (?, ?)";
        return (jdbcTemplate.update(sqlQuery, userId, filmId) > 0);
    }

    @Override
    public boolean deleteLike(long filmId, long userId) {
        String sqlQuery = "DELETE FROM LIKES WHERE USER_ID = ? AND FILM_ID = ?";
        return (jdbcTemplate.update(sqlQuery, userId, filmId) > 0);
    }

    @Override
    public List<Film> getPopularFilms(int count, Map<String, String> params) {
        StringBuilder sb = new StringBuilder();
        List<String> conditions = new ArrayList<>();
        List<Object> values = new ArrayList<>();

        if (params.containsKey("count") && params.keySet().size() > 1) {
            sb.append("WHERE ");
        }

        if (params.containsKey("genreId")) {
            if (Integer.parseInt(params.get("genreId")) < 1)
                throw new ValidationException("Указан отрицательный ID жанра!");
            conditions.add("fg.GENRE_ID = ? ");
            values.add(params.get("genreId"));
        }

        if (params.containsKey("year")) {
            conditions.add("EXTRACT(YEAR FROM f.RELEASE_DATE) = ? ");
            values.add(params.get("year"));
        }

        for (int i = 0; i < conditions.size(); i++) {
            sb.append(conditions.get(i));
            if (i != conditions.size() - 1) {
                sb.append("AND ");
            }
        }
        values.add(count);

        final String sqlQuery =
                "SELECT " +
                        "f.*, " +
                        "mpa.MPA_NAME, " +
                        "COUNT(L.USER_ID)" +
                        "FROM FILMS AS f " +
                        "LEFT JOIN LIKES AS l ON f.film_id = l.film_id " +
                        "LEFT JOIN MPA AS mpa ON F.MPA_ID = mpa.MPA_ID " +
                        "LEFT JOIN FILM_GENRES AS fg ON f.FILM_ID = fg.FILM_ID " +
                        (params.size() > 0 ? sb.toString() : "") +
                        " GROUP BY F.film_id, film_name, description, duration, f.mpa_id, mpa.mpa_id, mpa.mpa_name, release_date " +
                        "ORDER BY COUNT(L.USER_ID) DESC " +
                        "LIMIT ?";

        return jdbcTemplate.query(sqlQuery, FilmDbStorage::makeFilm, values.toArray(new Object[0]));
    }

    @Override
    public Mpa findMpaById(long id) throws ObjectNotFoundException {
        final String sqlQuery = "SELECT * FROM MPA WHERE MPA_ID = ?";
        final List<Mpa> mpa = jdbcTemplate.query(sqlQuery, FilmDbStorage::makeMpa, id);
        if (mpa.size() == 0) {
            log.debug(String.format("Неизвестный рейтинг %d.", id));
            throw new ObjectNotFoundException("Неизвестный рейтинг");
        }
        return mpa.get(0);
    }

    @Override
    public List<Mpa> findAllMpa() {
        final String sqlQuery = "SELECT * FROM MPA";
        final List<Mpa> mpa = jdbcTemplate.query(sqlQuery, FilmDbStorage::makeMpa);
        if (mpa.size() == 0) {
            return Collections.emptyList();
        }
        return mpa;
    }

    @Override
    public Genre findGenreById(long id) throws ObjectNotFoundException {
        final String sqlQuery = "SELECT * FROM GENRES WHERE GENRE_ID = ?";
        final List<Genre> genre = jdbcTemplate.query(sqlQuery, FilmDbStorage::makeGenre, id);
        if (genre.size() == 0) {
            log.debug(String.format("Неизвестный жанр %d.", id));
            throw new ObjectNotFoundException("Неизвестный жанр");
        }
        return genre.get(0);
    }

    @Override
    public List<Genre> findAllGenre() {
        final String sqlQuery = "SELECT * FROM GENRES";
        final List<Genre> genres = jdbcTemplate.query(sqlQuery, FilmDbStorage::makeGenre);
        if (genres.size() == 0) {
            return Collections.emptyList();
        }
        return genres;
    }


    private Film setGenre(Film film) {
        final String sqlQueryGenre = "SELECT G.GENRE_ID, G.GENRE_NAME FROM FILM_GENRES FG " +
                "LEFT JOIN GENRES G ON G.GENRE_ID = FG.GENRE_ID " +
                "WHERE FG.FILM_ID = ?";
        List<Genre> genres = jdbcTemplate.query(sqlQueryGenre, FilmDbStorage::makeGenre, film.getId());
        film.setGenres(genres);
        return film;
    }

    //МАППЕРЫ
    public static Film makeFilm(ResultSet rs, int rowNum) throws SQLException {
        return Film.builder()
                .id(rs.getInt("FILM_ID"))
                .name(rs.getString("FILM_NAME"))
                .description(rs.getString("DESCRIPTION"))
                .releaseDate(rs.getDate("RELEASE_DATE").toLocalDate())
                .duration(rs.getInt("DURATION"))
                .mpa(new Mpa(rs.getInt("MPA_ID"), rs.getString("MPA_NAME")))
                .build();
    }

    public static Genre makeGenre(ResultSet rs, int rowNum) throws SQLException {
        return new Genre(rs.getInt("GENRE_ID"), rs.getString("GENRE_NAME"));
    }

    public static Mpa makeMpa(ResultSet rs, int rowNum) throws SQLException {
        return new Mpa(rs.getInt("MPA_ID"), rs.getString("MPA_NAME"));
    }
}
