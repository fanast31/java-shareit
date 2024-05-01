package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDtoRequest;
import ru.practicum.shareit.user.dto.UserDtoResponse;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    UserDtoResponse create(UserDtoRequest userDto);

    UserDtoResponse update(long userId, UserDtoRequest userDto);

    User getUser(long userId);

    UserDtoResponse getUserDtoResponse(long userId);

    List<UserDtoResponse> getAll();

    void delete(long userId);
}
