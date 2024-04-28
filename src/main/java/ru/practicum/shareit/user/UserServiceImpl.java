package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.DataNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDto create(UserDto userDto) {
        return UserMapper.toUserDto(userRepository.save(UserMapper.toUser(userDto)));
    }

    @Override
    public UserDto update(long userId, UserDto userDto) {

        User userDB = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("User not found"));

        User user = UserMapper.toUser(userDto);
        user.setId(userDB.getId());
        if (user.getName() == null) {
            user.setName(userDB.getName());
        }
        if (user.getEmail() == null) {
            user.setEmail(userDB.getEmail());
        }

        return UserMapper.toUserDto(userRepository.save(user));
    }

    @Override
    public User getUser(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("User not found"));
    }

    @Override
    public UserDto getUserDto(long userId) {
        return UserMapper.toUserDto(userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("User not found")));
    }

    @Override
    public List<UserDto> getAll() {
        return userRepository.findAll().stream()
                        .map(UserMapper::toUserDto)
                        .collect(Collectors.toList());
    }

    @Override
    public void delete(long userId) {
        User userDB = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("User not found"));
        userRepository.delete(userDB);
    }

}
