package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.interfaces.FeedService;
import ru.yandex.practicum.filmorate.interfaces.FeedStorage;
import ru.yandex.practicum.filmorate.model.Event;

import java.util.List;

@Slf4j
@Service
public class FeedServiceImpl implements FeedService {
    private final FeedStorage feedStorage;

    public FeedServiceImpl(FeedStorage feedStorage) {
        this.feedStorage = feedStorage;
    }

    @Override
    public List<Event> getFeed(long id) {
        return feedStorage.getFeed(id);
    }
}
