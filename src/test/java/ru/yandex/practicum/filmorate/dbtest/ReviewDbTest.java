package ru.yandex.practicum.filmorate.dbtest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;
import ru.yandex.practicum.filmorate.storage.ReviewDbStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ReviewDbTest {

    private final UserDbStorage userStorage;
    private final FilmDbStorage filmStorage;
    private final ReviewDbStorage reviewStorage;
    private User user;
    private Film film;
    private Review review;
    private Review newReview;

    @BeforeEach
    public void BeforeEach() {
        user = User.builder()
                .name("Тест")
                .email("test@ya.ru")
                .login("test")
                .birthday(LocalDate.parse("1990-05-05"))
                .build();
        User userAdd = userStorage.create(user);
        film = Film.builder()
                .name("Тест фильм")
                .description("Тест описание фильма")
                .releaseDate(LocalDate.parse("2022-07-14"))
                .mpa(new Mpa(1, "G"))
                .duration(100)
                .build();
        Film filmAdd = filmStorage.create(film);
        review = Review.builder()
                .content("Это позитивный отзыв для фильма")
                .isPositive(true)
                .userId(userAdd.getId())
                .filmId(filmAdd.getId())
                .useful(0)
                .build();
    }

    @Test
    void checkCreateReview() {
        Review reviewAdd = reviewStorage.create(review);
        int actualResult = reviewStorage.getAllReviews().size();
        assertEquals(1, actualResult);
    }
}