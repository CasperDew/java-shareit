package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserServiceImplTest {
    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;


    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        user1 = new User();
        user1.setName("User1");
        user1.setEmail("user1@test.com");

        user2 = new User();
        user2.setName("User2");
        user2.setEmail("user2@test.com");
    }

    @Test
    void createUserSuccess() {
        UserDto result = userService.createUser(user1);

        assertThat(result.getId()).isNotNull();
        assertThat(result.getName()).isEqualTo("User1");
        assertThat(result.getEmail()).isEqualTo("user1@test.com");

        assertThat(userRepository.count()).isEqualTo(1);
    }

    @Test
    void createUserEmailConflict() {
        userService.createUser(user1);

        assertThatThrownBy(() -> userService.createUser(user1))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("Такой Email уже используется.");
    }

    @Test
    void updateUserSuccess() {
        UserDto created = userService.createUser(user1);
        Long userId = created.getId();

        User updatedUser = new User();
        updatedUser.setId(userId);
        updatedUser.setName("Updated Name");
        updatedUser.setEmail("updated@test.com");

        UserDto result = userService.updateUser(updatedUser);

        assertThat(result.getName()).isEqualTo("Updated Name");
        assertThat(result.getEmail()).isEqualTo("updated@test.com");
    }

    @Test
    void getUsersSuccess() {
        userService.createUser(user1);
        userService.createUser(user2);

        List<UserDto> users = userService.getUsers();

        assertThat(users).hasSize(2);
        assertThat(users.get(0).getName()).isEqualTo("User1");
        assertThat(users.get(1).getName()).isEqualTo("User2");
    }

    @Test
    void getUserForIdSuccess() {
        UserDto created = userService.createUser(user1);
        Long userId = created.getId();

        UserDto found = userService.getUserById(userId);

        assertThat(found.getId()).isEqualTo(userId);
        assertThat(found.getName()).isEqualTo("User1");
        assertThat(found.getEmail()).isEqualTo("user1@test.com");
    }

    @Test
    void getUserForIdNotFound() {
        assertThatThrownBy(() -> userService.getUserById(999L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Пользователь не найден");
    }

    @Test
    void deleteUserForIdSuccess() {
        UserDto created = userService.createUser(user1);
        Long userId = created.getId();

        userService.deleteUserById(userId);

        assertThat(userRepository.findById(userId)).isEmpty();
    }
}
