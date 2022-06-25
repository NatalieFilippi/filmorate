package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
public class User {
    private long id;
    @NotBlank
    @Email
    private String email;
    @NotBlank
    private String login;
    private String name;
    @Past
    private LocalDate birthday;
    private HashSet<Long> friends;

    public long addFriend(long id) {
        if (friends == null) {
            friends = new HashSet<>();
        }
        friends.add(id);
        return id;
    }

    public long deleteFriend(long id) {
        if (friends == null) {
            friends = new HashSet<>();
        }
        friends.remove(id);
        return id;
    }

}
