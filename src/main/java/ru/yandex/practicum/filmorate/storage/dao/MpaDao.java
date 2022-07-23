package ru.yandex.practicum.filmorate.storage.dao;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

@Repository
public class MpaDao {
    private final JdbcTemplate jdbcTemplate;

    public MpaDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Mpa findMpaById(Integer id) {
        try {
            String sql = "SELECT * FROM MPA where mpa_id = ?";
            return jdbcTemplate.queryForObject(sql, this::makeMPA, id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public List<Mpa> findAllMpa() {
        String sql = "SELECT * FROM MPA";
        return jdbcTemplate.query(sql, this::makeMPA);
    }

    /*
    @Override
    public Mpa findMpaById(int id) throws ObjectNotFoundException {
        final String sqlQuery = "select * from MPA where MPA_ID = ?";
        final List<Mpa> mpa = jdbcTemplate.query(sqlQuery, FilmDbStorage::makeMpa, id);
        if (mpa.size() == 0) {
            log.debug(String.format("Неизвестный рейтинг %d.", id));
            throw new ObjectNotFoundException("Неизвестный рейтинг");
        }
        return mpa.get(0);
    }

    @Override
    public List<Mpa> findAllMpa() {
        final String sqlQuery = "select * from MPA";
        final List<Mpa> mpa = jdbcTemplate.query(sqlQuery, FilmDbStorage::makeMpa);
        if (mpa.size() == 0) {
            return Collections.emptyList();
        }
        return mpa;
    }
    * */

    private Mpa makeMPA(ResultSet rs, int rowNum) throws SQLException {
        Integer id = rs.getInt("mpa_id");
        String name = rs.getString("mpa_name");
        return new Mpa(id, name);
    }
}
