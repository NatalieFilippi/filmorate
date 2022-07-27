package ru.yandex.practicum.filmorate.dbtest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.interfaces.FeedStorage;
import ru.yandex.practicum.filmorate.interfaces.UserService;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FeedTest {

    private final UserService userService;
    private final UserDbStorage userStorage;
    private final FeedStorage feedStorage;

    @BeforeEach
    private void beforeEach() throws ValidationException {
        User user = User.builder()
                .name("Тест")
                .email("test@ya.ru")
                .login("test")
                .birthday(LocalDate.parse("1990-05-05"))
                .build();
        userStorage.create(user);
        User friend = User.builder()
                .name("Друг")
                .email("test_friend@ya.ru")
                .login("friend")
                .birthday(LocalDate.parse("1989-05-05"))
                .build();
        userStorage.create(friend);
    }

    @AfterEach
    private void afterEach() {
        userStorage.deleteAll();
    }

    @Test
    public void testGetFeedEmpty() throws ObjectNotFoundException {
        List<Event> feed = feedStorage.getFeed(1);
        assertEquals(feed.size(), 0);
    }

    @Test
    public void testGetFeed() throws ObjectNotFoundException {
        List<User> users = userService.findAll();
        userService.addFriend(users.get(0).getId(),users.get(1).getId());
        List<Event> feed = feedStorage.getFeed(users.get(0).getId());
        assertEquals(feed.size(), 1);

        userService.deleteFriend(users.get(0).getId(),users.get(1).getId());
        feed = feedStorage.getFeed(users.get(0).getId());
        assertEquals(feed.size(), 2);
        feed = feedStorage.getFeed(users.get(1).getId());
        assertEquals(feed.size(), 0);
    }
}
