package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
//todo delete
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class User {
    @EqualsAndHashCode.Include private long id;
    private String email;
    private String login;
    private String name;
    private LocalDate birthday;
    private Set<Integer> friends;

    @JsonCreator
    public User(String email, String login,
                String name, LocalDate birthday)
    {
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
        this.friends = new HashSet<>();
    }
}
