package ru.yandex.practicum.filmorate.interfaces;

import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.SQLException;
import java.util.List;

public interface FeedStorage {
    List<Event> getFeed(long id);
    void eventRegistration(Event event) throws SQLException;
}
