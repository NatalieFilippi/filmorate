package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/reviews")
public class ReviewController {
    private final ReviewService reviewService;

    @Autowired
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    //TODO ДОПИСАТЬ ДОКУ
    @PostMapping
    public Review create(@RequestBody Review review) throws ObjectNotFoundException {
        log.info("Успешно добавлен отзыв: {}", review.getContent());
        return reviewService.create(review);
    }

    //TODO ДОПИСАТЬ ДОКУ
    @GetMapping("/{reviewId}")
    public Review getById(@PathVariable int reviewId) throws ObjectNotFoundException {
        log.info("Успешно найден отзыв с ID: {}", reviewId);
        return reviewService.getById(reviewId);
    }

    //TODO ДОПИСАТЬ ДОКУ
    @PutMapping
    public Review update(@RequestBody Review review) throws ObjectNotFoundException {
        log.info("Успешно обновлен отзыв: {}", review.getContent());
        return reviewService.update(review);
    }

    //TODO ДОПИСАТЬ ДОКУ
    @DeleteMapping("/{reviewId}")
    public void deleteById(@PathVariable int reviewId) {
        log.info("Успешно удален отзыв с ID: {}", reviewId);
        reviewService.deleteById(reviewId);
    }

    //TODO ДОПИСАТЬ ДОКУ
    @GetMapping
    public List<Review> getReviewsForFilm(
            @RequestParam(value = "filmId", defaultValue = "-1", required = false) Long filmId,
            @RequestParam(value = "count", defaultValue = "10", required = false) int count){
        if (filmId == -1) {
            return reviewService.getAllReviews();
        }
        log.info("Для фильма с ID: {} содержится отзывов: {}", filmId, count);
        return reviewService.getReviewsForFilm(filmId, count);
    }

    //TODO ДОПИСАТЬ ДОКУ
    @PutMapping("/{reviewId}/like/{userId}")
    public void addLikeReview(@PathVariable int reviewId,
                              @PathVariable Long userId) throws ObjectNotFoundException {
        log.info("Для отзыва с ID: {} добавлен лайк от пользователя c ID: {}", reviewId, userId);
        reviewService.addLikeReview(reviewId, userId);
    }

    //TODO ДОПИСАТЬ ДОКУ
    @PutMapping("/{reviewId}/dislike/{userId}")
    public void addDislikeReview(@PathVariable int reviewId,
                                 @PathVariable Long userId) throws ObjectNotFoundException {
        log.info("Для отзыва с ID: {} добавлен дизлайк от пользователя c ID: {}", reviewId, userId);
        reviewService.addDislikeReview(reviewId, userId);
    }

    //TODO ДОПИСАТЬ ДОКУ
    @DeleteMapping("/{reviewId}/like/{userId}")
    public void removeLikeFromReview(@PathVariable int reviewId,
                                     @PathVariable Long userId) throws ObjectNotFoundException {
        log.info("Для отзыва с ID: {} удален лайк от пользователя c ID: {}", reviewId, userId);
        reviewService.deleteLikeFromReview(reviewId, userId);
    }

    //TODO ДОПИСАТЬ ДОКУ
    @DeleteMapping("/{reviewId}/dislike/{userId}")
    public void removeDislikeFromReview(@PathVariable int reviewId,
                                        @PathVariable Long userId) throws ObjectNotFoundException {
        log.info("Для отзыва с ID: {} удален дизлайк от пользователя c ID: {}", reviewId, userId);
        reviewService.deleteDislikeFromReview(reviewId, userId);
    }
}