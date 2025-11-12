package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserRepository {
    UserDto createUser(User user);

    UserDto updateUser(User user);

    List<UserDto> getUsers();

    UserDto getUserById(Long userId);

    void deleteUserById(Long userId);
}
