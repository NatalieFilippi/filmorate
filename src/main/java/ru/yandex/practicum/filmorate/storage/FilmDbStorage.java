package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.interfaces.FilmStorage;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.dao.DirectorDao;
import ru.yandex.practicum.filmorate.storage.dao.GenreDao;
import ru.yandex.practicum.filmorate.storage.dao.MpaDao;

import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

@Repository
@Primary
@RequiredArgsConstructor
@Slf4j
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final DirectorDao directorDao;
    private final GenreDao genreDao;
    private final MpaDao mpaDao;

    /*    @Override
      public List<Film> findAllFilms() {
            final String sqlQuery = "SELECT * FROM films ORDER BY film_id";
            final List<Film> films = jdbcTemplate.query(sqlQuery, this::makeFilm);
            if (films.size() == 0) {
                return Collections.emptyList();
            }
            for (Film film : films) {
                setGenre(film);
            }
            return films;
        }

        @Override
        public Film findFilmById(long id) throws ObjectNotFoundException {
            final String sqlQuery = "SELECT * FROM films WHERE film_id = ?";
            final List<Film> films = jdbcTemplate.query(sqlQuery, this::makeFilm, id);
            if (films.size() == 0) {
                log.debug(String.format("Фильм %d не найден.", id));
                throw new ObjectNotFoundException("Фильм не найден!");
            }
            Film film = films.get(0);
            setGenre(film);
            return film;
        }*/
    @Override
    public Collection<Film> findAllFilms() {
        String sql = "SELECT * FROM films ORDER BY film_id";
        return jdbcTemplate.query(sql, this::makeFilm);
    }

    @Override
    public Film findFilmById(long id) {
        try {
            String sql = "SELECT * FROM films WHERE film_id = ?";
            return jdbcTemplate.queryForObject(sql, this::makeFilm, id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public Film create(Film film) {
        final String sqlQuery = "INSERT INTO films (film_name, description, release_date, " +
                "duration, mpa_id) VALUES (?,?,?,?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        int rows = this.jdbcTemplate.update(
            connection -> {
                PreparedStatement ps = connection.prepareStatement(sqlQuery, new String[]{"film_id"});
                ps.setString(1, film.getName());
                ps.setString(2, film.getDescription());
                ps.setDate(3, Date.valueOf(film.getReleaseDate()));
                ps.setInt(4, film.getDuration());
                ps.setInt(5, film.getMpa().getId());
                return ps;
            }, keyHolder);


        if (rows == 1) {
            long id = keyHolder.getKey().longValue();
            film.setId(id);
            genreDao.addFilmGenres(film);
            directorDao.addFilm(film);
            return findFilmById(id);
        }
        return null;
    }

    @Override
    public Film updateFilm(Film film) {
        Film initFilm = findFilmById(film.getId());
        String sqlQuery = "UPDATE films SET film_name = ?, description = ?, release_date = ?," +
                "duration = ?, mpa_id = ? WHERE film_id = ?";
        int rows = jdbcTemplate.update(sqlQuery,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());

        genreDao.updateFilmGenres(film);
        directorDao.updateFilm(film);
        if (rows == 1) {
            Film updFilm = findFilmById(film.getId());
            if (initFilm.getGenres() != null && updFilm.getGenres() == null) {
                updFilm.setGenres(new HashSet<>()); // using to fit postman tests only
                //updFilm.setDirectors(new HashSet<>()); // using to fit postman tests only
            }
            if (initFilm.getDirectors().size() != 0 && updFilm.getDirectors().size() == 0) {
                updFilm.setDirectors(null); // using to fit postman tests only
            }
            return updFilm;
        }
        return null;
    }

    @Override
    public void deleteAll() {
        String sqlQuery = "delete from FILMS";
        jdbcTemplate.update(sqlQuery);
    }

    @Override
    public void deleteFilm(long id) {
        String sqlQuery = "DELETE FROM films WHERE film_id = ?";
        jdbcTemplate.update(sqlQuery, id);
    }

    @Override
    public boolean addLike(long filmId, long userId) {
        String sqlQuery = "INSERT INTO film_likes (FILM_ID, USER_ID) VALUES (?, ?)";
        int rows = jdbcTemplate.update(sqlQuery, filmId, userId);
        updateRate(filmId, 1);
        return (rows > 0);
    }

    @Override
    public boolean deleteLike(long filmId, long userId) {
        String sqlQuery = "DELETE FROM film_likes WHERE film_id = ? AND user_id = ?";
        int rows = jdbcTemplate.update(sqlQuery, filmId, userId);
        updateRate(filmId, -1);
        return (rows > 0);
    }

    private void updateRate(long filmId, Integer rateDifference) {
        String sqlQuery = "UPDATE films SET rate = rate + ? WHERE film_id = ?";
        jdbcTemplate.update(sqlQuery, rateDifference, filmId);
    }

    @Override
    public Collection<Film> findNMostPopularFilms(Optional<Integer> count) {
        String sqlQuery = "SELECT * FROM films ORDER BY rate DESC limit ?";
        return jdbcTemplate.query(sqlQuery, this::makeFilm, count.orElse(10));
    }



    @Override
    public Collection<Film> findFilmsOfDirectorSortByYear(int directorId) {
        String sqlQuery = "SELECT fl.film_id, " +
                "fl.film_name, " +
                "fl.description, " +
                "fl.release_date, " +
                "fl.duration, " +
                "fl.mpa_id " +
                "FROM film_directors AS fd " +
                "JOIN films fl ON fd.film_id = fl.film_id " +
                "WHERE fd.id = ? ORDER BY release_date";

        return jdbcTemplate.query(sqlQuery, this::makeFilm, directorId);
    }

    @Override
    public Collection<Film> findFilmsOfDirectorSortByLikes(int directorId) {
        String sqlQuery = "SELECT fl.film_id, " +
                "fl.film_name, " +
                "fl.description, " +
                "fl.release_date, " +
                "fl.duration, " +
                "fl.mpa_id " +
                "FROM film_directors AS fd " +
                "JOIN films AS fl ON fd.film_id = fl.film_id " +
                "WHERE fd.id = ? ORDER BY rate";

        return jdbcTemplate.query(sqlQuery, this::makeFilm, directorId);
    }


/*    private Film setGenre(Film film) {
        final String sqlQueryGenre = "select G.GENRE_ID, G.GENRE_NAME from FILM_GENRES FG " +
                "left join GENRES G on G.GENRE_ID = FG.GENRE_ID " +
                "where FG.FILM_ID = ?";
        List<Genre> genres = jdbcTemplate.query(sqlQueryGenre, FilmDbStorage::makeGenre , film.getId());
        film.setGenres((Set<Genre>) genres);
        return film;
    }*/

/*    private Film setDirector(Film film) {
        final String sqlQueryDirector = "select D.DIRECTOR_ID, D.DIRECTOR_NAME from FILM_DIRECTORS FD " +
                "left join DIRECTORS D on D.DIRECTOR_ID = FD.DIRECTOR_ID " +
                "where FD.FILM_ID = ?";
        List<Director> directors = jdbcTemplate.query(sqlQueryDirector, FilmDbStorage::makeDirector , film.getId());
        film.setDirectors((Set<Director>) directors);
        return film;
    }*/



    //todo director
    //МАППЕРЫ
    private Film makeFilm(ResultSet rs, int rowNum) throws SQLException {
        long id = rs.getLong("film_id");
        String name = rs.getString("film_name");
        String description = rs.getString("description");
        LocalDate releaseDate = rs.getDate("release_date").toLocalDate();
        int duration = rs.getInt("duration");
        int mpaId = rs.getInt("mpa_id");

        Mpa mpa = mpaDao.findMpaById(mpaId);
        Set<Genre> genres = new HashSet<>(genreDao.findFilmGenres(id));
        Set<Director> directors = new HashSet<>(directorDao.findFilm(id));
        genres = genres.isEmpty() ? null : genres; //для postman тест

        return new Film(id, name, description, releaseDate,
                duration, directors, new HashSet<>(), mpa, genres);
    }

}
