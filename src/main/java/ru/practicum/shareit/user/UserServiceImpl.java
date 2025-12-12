package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
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
        return UserMapper.mapToUSerDto(userRepository.save(user));
    }

    @Override
    public UserDto updateUser(User user) {
        validateEmail(user);
        User updateUser = UserMapper.updateFieldsUser(user, userRepository.findById(user.getId()).get());
        return UserMapper.mapToUSerDto(userRepository.save(updateUser));
    }

    @Override
    public List<UserDto> getUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserMapper::mapToUSerDto)
                .toList();
    }

    @Override
    public UserDto getUserById(Long userId) {
        if (userRepository.findById(userId).isPresent()) {
            return UserMapper.mapToUSerDto(userRepository.findById(userId).get());
        } else {
            throw new NotFoundException("Пользователь не найден");
        }
    }

    @Override
    public void deleteUserById(Long userId) {
        userRepository.deleteById(userId);
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
