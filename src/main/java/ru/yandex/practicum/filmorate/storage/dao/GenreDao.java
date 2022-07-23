package ru.yandex.practicum.filmorate.storage.dao;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
public class GenreDao {

    private final JdbcTemplate jdbcTemplate;

    public GenreDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Genre findGenre(Integer id) {
        try {
            String sql = "select * from film_genres where genre_id = ?";
            return jdbcTemplate.queryForObject(sql, this::makeGenre, id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

/*    public Genre findGenreById(long id) throws ObjectNotFoundException {
        final String sqlQuery = "SELECT * FROM genres where genre_id = ?";
        final List<Genre> genre = jdbcTemplate.query(sqlQuery, FilmDbStorage::makeGenre, id);
        if (genre.size() == 0) {
            log.debug(String.format("Неизвестный жанр %d.", id));
            throw new ObjectNotFoundException("Неизвестный жанр");
        }
        return genre.get(0);
    }*/

    public List<Genre> findAllGenres() {
        String sql = "select * from film_genres";
        return jdbcTemplate.query(sql, this::makeGenre);
    }

/*    public List<Genre> findAllGenres() {
        final String sqlQuery = "SELECT * FROM genres";
        final List<Genre> genres = jdbcTemplate.query(sqlQuery, FilmDbStorage::makeGenre);
        if (genres.size() == 0) {
            return Collections.emptyList();
        }
        return genres;
    }*/

    public List<Genre> findFilmGenres(long id) {
        String sql = "SELECT fg.genre_id, g.genre_name " +
                "FROM film_genres fg " +
                "JOIN genres g on g.genre_id = fg.genre_id " +
                "WHERE film_id = ?";

        return jdbcTemplate.query(sql, this::makeGenre, id);
    }

    public void addFilmGenres(Film film) {
        Set<Genre> genres = film.getGenres();

        if (!(genres == null || genres.isEmpty())) {
            String sqlQuery = "insert into film_genres values (?,?)";
            genres.stream().forEach(x ->
                    jdbcTemplate.update(sqlQuery, film.getId(), x.getId()));
        }
    }

    public void updateFilmGenres(Film film) {
        Set<Genre> newGenres = film.getGenres();
        Set<Genre> currentGenres = new HashSet<>(findFilmGenres(film.getId()));

        if (!Objects.equals(newGenres, currentGenres)) {
            deleteFilmGenres(film);
            addFilmGenres(film);
        }
    }

    public void deleteFilmGenres(Film film) {
        String sql = "delete from film_genres where film_id = ?";
        jdbcTemplate.update(sql, film.getId());
    }

    private Genre makeGenre(ResultSet rs, int rowNum) throws SQLException {
        Integer id = rs.getInt("genre_id");
        String  name = rs.getString("genre_name");
        return new Genre(id, name);
    }

}
