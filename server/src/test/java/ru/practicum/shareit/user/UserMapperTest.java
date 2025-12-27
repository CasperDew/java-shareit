package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import static org.assertj.core.api.Assertions.assertThat;

public class UserMapperTest {
    @Test
    void mapToUserAllFieldsCopied() {
        UserDto dto = new UserDto();
        dto.setId(100L);
        dto.setName("Alice");
        dto.setEmail("alice@example.com");

        User user = UserMapper.mapToUser(dto);

        assertThat(user).isNotNull();
        assertThat(user.getId()).isEqualTo(100L);
        assertThat(user.getName()).isEqualTo("Alice");
        assertThat(user.getEmail()).isEqualTo("alice@example.com");
    }

    @Test
    void mapToUserNullFieldsHandled() {
        UserDto dto = new UserDto();
        dto.setId(null);
        dto.setName(null);
        dto.setEmail(null);

        User user = UserMapper.mapToUser(dto);

        assertThat(user.getId()).isNull();
        assertThat(user.getName()).isNull();
        assertThat(user.getEmail()).isNull();
    }

    @Test
    void mapToUserOriginalDtoNotModified() {
        UserDto dto = new UserDto();
        dto.setName("Original");

        UserMapper.mapToUser(dto);

        assertThat(dto.getName()).isEqualTo("Original");
    }

    @Test
    void mapToUserDtoAllFieldsCopied() {
        User user = new User();
        user.setId(200L);
        user.setName("Bob");
        user.setEmail("bob@example.com");

        UserDto dto = UserMapper.mapToUSerDto(user);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(200L);
        assertThat(dto.getName()).isEqualTo("Bob");
        assertThat(dto.getEmail()).isEqualTo("bob@example.com");
    }

    @Test
    void mapToUserDtoNullFieldsHandled() {
        User user = new User();
        user.setId(null);
        user.setName(null);
        user.setEmail(null);

        UserDto dto = UserMapper.mapToUSerDto(user);

        assertThat(dto.getId()).isNull();
        assertThat(dto.getName()).isNull();
        assertThat(dto.getEmail()).isNull();
    }

    @Test
    void mapToUserDtoOriginalUserNotModified() {
        User user = new User();
        user.setName("Original User");

        UserMapper.mapToUSerDto(user);

        assertThat(user.getName()).isEqualTo("Original User");
    }

    @Test
    void updateFieldsUserUpdatesNonNullFields() {
        User source = new User();
        source.setName("Updated Name");
        source.setEmail("updated@example.com");

        User target = new User();
        target.setId(999L);
        target.setName("Old Name");
        target.setEmail("old@example.com");

        User result = UserMapper.updateFieldsUser(source, target);

        assertThat(result.getId()).isEqualTo(999L);
        assertThat(result.getName()).isEqualTo("Updated Name");
        assertThat(result.getEmail()).isEqualTo("updated@example.com");
    }

    @Test
    void updateFieldsUserIgnoresNullFields() {
        User source = new User();
        source.setName(null);      // null → не обновляем
        source.setEmail("new@email.com");

        User target = new User();
        target.setName("Existing Name");
        target.setEmail("old@email.com");

        User result = UserMapper.updateFieldsUser(source, target);

        assertThat(result.getName()).isEqualTo("Existing Name");
        assertThat(result.getEmail()).isEqualTo("new@email.com");
    }

    @Test
    void updateFieldsUserTargetUnchangedIfSourceIsNull() {
        User source = new User();  // все поля null
        User target = new User();
        target.setId(123L);
        target.setName("Target Name");
        target.setEmail("target@example.com");

        User result = UserMapper.updateFieldsUser(source, target);

        assertThat(result.getId()).isEqualTo(123L);
        assertThat(result.getName()).isEqualTo("Target Name");
        assertThat(result.getEmail()).isEqualTo("target@example.com");
    }

    @Test
    void updateFieldsUserReturnsSameTargetInstance() {
        User source = new User();
        User target = new User();

        User result = UserMapper.updateFieldsUser(source, target);

        assertThat(result).isSameAs(target);
    }
}
