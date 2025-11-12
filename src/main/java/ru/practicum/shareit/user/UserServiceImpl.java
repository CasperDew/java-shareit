package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDto createUser(User user) {
        validateEmail(user);
        return userRepository.createUser(user);
    }

    @Override
    public UserDto updateUser(User user) {
        validateEmail(user);
        return userRepository.updateUser(user);
    }

    @Override
    public List<UserDto> getUsers() {
        return userRepository.getUsers();
    }

    @Override
    public UserDto getUserById(Long userId) {
        return userRepository.getUserById(userId);
    }

    @Override
    public void deleteUserById(Long userId) {
        userRepository.deleteUserById(userId);
    }

    private void validateEmail(User user) {
        if (user.getEmail() != null) {
            for (UserDto userStorage : getUsers()) {
                if (user.getEmail().equals(userStorage.getEmail())) {
                    throw new ConflictException("Такой Email уже используется.");
                }
            }
        }
    }
}
