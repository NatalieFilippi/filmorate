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
            String sql = "select * from genres where genre_id = ?";
            return jdbcTemplate.queryForObject(sql, this::makeGenre, id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public List<Genre> findAllGenres() {
        String sql = "select * from genres";
        return this.jdbcTemplate.query(sql, this::makeGenre);
    }

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
