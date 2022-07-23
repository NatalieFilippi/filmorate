package yandex.practicum.filmorate.dbtest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserDbTest {

    private final UserDbStorage userStorage;

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
    public void testFindUserById() throws ObjectNotFoundException {
        List<User> users = userStorage.findAll();
        Long id = users.get(0).getId();
        Optional<User> userOptional = Optional.ofNullable(userStorage.findById(id));

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", id)
                );
    }

    @Test
    public void testFindUserByIdUnknown() throws ObjectNotFoundException {
        ObjectNotFoundException ex = assertThrows(ObjectNotFoundException.class, ()->userStorage.findById(-1));
        assertEquals(ex.getMessage(), "Пользователь не найден!");
    }

    @Test
    public void testFindAllUser() throws ObjectNotFoundException {
        List<User> users = userStorage.findAll();
        assertEquals(users.size(), 2);
    }

    @Test
    public void testUpdateUser() throws ObjectNotFoundException, ValidationException {
        List<User> users = userStorage.findAll();
        User user = users.get(0);
        user.setName("Test update");
        userStorage.updateUser(user);
        Optional<User> userOptional = Optional.ofNullable(userStorage.findById(user.getId()));

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(u ->
                        assertThat(u).hasFieldOrPropertyWithValue("name", "Test update")
                );
    }

    @Test
    public void testDeleteUser() throws ObjectNotFoundException {
        List<User> users = userStorage.findAll();
        User user = users.get(0);
        userStorage.deleteUser(user.getId());
        assertEquals(userStorage.findAll().size(), 1);
    }

    @Test
    public void testAddFriend() {
        List<User> users = userStorage.findAll();
        User user = users.get(0);
        User friend = users.get(1);
        userStorage.addFriend(user.getId(), friend.getId());
        assertEquals(userStorage.findFriends(user.getId()).get(0).getId(),friend.getId());
    }

/*    @Test
    public void testDeleteFriend() {
        List<User> users = userStorage.findAll();
        User user = users.get(0);
        User friend = users.get(1);
        userStorage.addFriend(user.getId(), friend.getId());
        assertEquals(userStorage.deleteFriend(user.getId(), friend.getId()), true);
        assertEquals(userStorage.findFriends(user.getId()).size(),0);
    }*/

    @Test
    public void testGetFriends() {
        List<User> users = userStorage.findAll();
        User user = users.get(0);
        User friend = users.get(1);
        userStorage.addFriend(user.getId(), friend.getId());
        assertEquals(userStorage.findFriends(user.getId()).size(),1);
    }

    @Test
    public void testGetCommonFriends() {
        List<User> users = userStorage.findAll();
        User user = users.get(0);
        User friend = users.get(1);
        userStorage.addFriend(user.getId(), friend.getId());
        assertEquals(userStorage.findCommonFriends(user.getId(), friend.getId()).size(),0);

        User friendCommon = User.builder()
                .name("Общий друг")
                .email("test_common_friend@ya.ru")
                .login("common_friend")
                .birthday(LocalDate.parse("1988-05-05"))
                .build();
        userStorage.create(friendCommon);
        friendCommon.setId(userStorage.findAll().get(2).getId());
        userStorage.addFriend(user.getId(), friendCommon.getId());
        userStorage.addFriend(friend.getId(), friendCommon.getId());
        assertEquals(userStorage.findCommonFriends(user.getId(), friend.getId()).size(),1);
    }

}
