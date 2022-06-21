package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
public class Film {
    private long id;
    @NotBlank
    private String name;
    private String description;
    private LocalDate releaseDate;
    @Positive
    private int duration;

    public Set<User> getLikes() {
        if (likes == null) {
            likes = new HashSet<>();
        }
        return likes;
    }

    private Set<User> likes;

    public int addLike(User user) {
        if (likes == null) {
            likes = new HashSet<>();
        }
        likes.add(user);
        return likes.size();
    }

    public int deleteLike(User user) {
        if (likes == null) {
            likes = new HashSet<>();
        }
        likes.remove(user);
        return likes.size();
    }
}
