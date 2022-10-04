package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private long id;
    @NotBlank(message = "Поле email не может быть пустым")
    @Email(message = "Некорректный формат электронной почты")
    private String email;
    @NotBlank(message = "Поле login не может быть пустым")
    @Pattern(regexp = "^\\S*$", message = "Login не может содержать пробел")
    private String login;
    private String name;
    @Past
    private LocalDate birthday;
}
