package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.interfaces.UserStorage;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.*;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

@Repository
@Primary
@Slf4j
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<User> findAll() {
        final String sqlQuery = "SELECT * FROM USERS ORDER BY user_id";
        return jdbcTemplate.query(sqlQuery, this::makeUser);
    }

    @Override
    public User findById(long id) {
        try {
            final String sqlQuery = "select * from USERS where USER_ID = ?";
            return jdbcTemplate.queryForObject(sqlQuery, this::makeUser, id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public User create(User user) {
        String sqlQuery = "insert into USERS (EMAIL, LOGIN, USER_NAME, BIRTHDAY) " +
                "values (?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        String name = (user.getName() == null || user.getName().isEmpty())
                ? user.getLogin() : user.getName();

        int rows = jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"USER_ID"});
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getLogin());
            stmt.setString(3, name);
            stmt.setDate(4, Date.valueOf(user.getBirthday()));
            return stmt;
        }, keyHolder);

        if (rows == 1) {
            long id = keyHolder.getKey().longValue();
            return findById(id);
        }
        return null;
    }

    @Override
    public User updateUser(User user) {
        String sqlQuery = "update USERS set " +
                "EMAIL = ?, LOGIN = ?, USER_NAME = ?, BIRTHDAY = ? " +
                "where USER_ID = ?";
        int rows = jdbcTemplate.update(sqlQuery,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId());
        if (rows == 1) {
            return findById(user.getId());
        }
        return null;
    }

    @Override
    public void deleteAll() {
        String sqlQuery = "delete from users";
        jdbcTemplate.update(sqlQuery);
    }

    public void deleteUser(long id) {
        String sqlQuery = "delete from USERS where USER_ID = ?";
        jdbcTemplate.update(sqlQuery, id);
    }

    public void addFriend(long userId, long friendId) {
        String sqlQuery = "insert into FRIENDS(USER_ID, FRIEND_ID)" + "values (?, ?)";
        jdbcTemplate.update(sqlQuery, userId, friendId);
        updateFriendshipStatus(userId, friendId, true);
    }

    @Override
    public void deleteFriend(long userId, long friendId) {
        String sqlQuery = "delete from FRIENDS where USER_ID = ? AND FRIEND_ID = ?";
        jdbcTemplate.update(sqlQuery, userId, friendId);
        updateFriendshipStatus(userId, friendId, false);
    }

    private void updateFriendshipStatus(long senderId, long recipientId, boolean status) {
        Collection<User> recipientFriends = findFriends(recipientId);

        if (!recipientFriends.isEmpty() && recipientFriends.contains(senderId)) {

            String query = "UPDATE friends set status = ? " +
                    "where (user_id = ? and friend_id = ?) " +
                    "or (user_id = ? and friend_id = ?)";

            jdbcTemplate.update(query, status, senderId, recipientId,
                    recipientId, senderId);
        }
    }

    @Override
    public Collection<User> findFriends(long userId) {
        String sqlQuery = "SELECT * FROM users WHERE user_id in " +
                "(SELECT friend_id FROM friends WHERE user_id = ?)";
        return jdbcTemplate.query(sqlQuery, this::makeUser, userId);
    }

    @Override
    public Collection<User> findCommonFriends(long userId, long otherId) {
        String sqlQuery = "SELECT * FROM users WHERE user_id in " +
                "(select distinct friend_id from friends " +
                "where user_id in (?, ?)" +
                "and friend_id not in (?, ?))";
        return jdbcTemplate.query(sqlQuery, this::makeUser, userId, otherId, userId, otherId);
    }

    private User makeUser(ResultSet rs, int rowNum) throws SQLException {
        Integer id = rs.getInt("user_id");
        String email = rs.getString("email");
        String login = rs.getString("login");
        String name = rs.getString("user_name");
        LocalDate birthday = rs.getDate("birthday").toLocalDate();

        return new User(id, email, login, name, birthday, new HashSet<>());
    }
}
