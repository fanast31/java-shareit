package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    UserDto create(UserDto userDto);

    UserDto update(long userId, UserDto userDto);

    User getUser(long userId);

    UserDto getUserDto(long userId);

    List<UserDto> getAll();

    void delete(long userId);
}
