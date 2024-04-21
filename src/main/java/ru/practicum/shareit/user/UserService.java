package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    public UserDto create(UserDto userDto);
    public UserDto update(long userId, User user);
    public UserDto get(long userId);
    public List<UserDto> getAll();
    public void delete(long userId);
}
