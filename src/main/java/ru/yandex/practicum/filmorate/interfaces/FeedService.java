package ru.yandex.practicum.filmorate.interfaces;

import ru.yandex.practicum.filmorate.model.Event;

import java.util.List;

public interface FeedService {
    List<Event> getFeed(long id);
}
