package ru.practicum.shareit.user;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class InMemoryUserStorage implements UserRepository {

    private static final String NOT_FOUND = "Пользователь не существует.";

    private final AtomicLong getNextId = new AtomicLong(1);
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public UserDto createUser(User user) {
        user.setId(getNextId.getAndIncrement());
        users.put(user.getId(), user);
        return UserMapper.mapToUSerDto(user);
    }

    @Override
    public UserDto updateUser(User user) {
        if (users.containsKey(user.getId())) {
            User updateUser = users.get(user.getId());
            User userResult = UserMapper.updateFieldsUser(user, updateUser);
            users.put(userResult.getId(), userResult);
            return UserMapper.mapToUSerDto(userResult);
        } else {
            throw new NotFoundException(NOT_FOUND);
        }
    }

    @Override
    public List<UserDto> getUsers() {
        return users.values().stream()
                .map(UserMapper::mapToUSerDto)
                .toList();
    }

    @Override
    public UserDto getUserById(Long userId) {
        Optional<User> user = Optional.ofNullable(users.get(userId));
        if (user.isPresent()) {
            return UserMapper.mapToUSerDto(user.get());
        } else {
            throw new NotFoundException(NOT_FOUND);
        }
    }

    @Override
    public void deleteUserById(Long userId) {
        if (users.containsKey(userId)) {
            users.remove(userId);
        } else {
            throw new NotFoundException(NOT_FOUND);
        }
    }
}
