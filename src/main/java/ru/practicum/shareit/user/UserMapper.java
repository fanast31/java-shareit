package ru.practicum.shareit.user;

import lombok.*;
import ru.practicum.shareit.user.dto.UserDtoRequest;
import ru.practicum.shareit.user.dto.UserDtoResponse;
import ru.practicum.shareit.user.model.User;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserMapper {

    public static UserDtoResponse toUserDtoResponse(User user) {
        if (user == null) {
            return null;
        }
        return UserDtoResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .build();
    }

    public static User toUser(UserDtoRequest userDtoRequest) {
        if (userDtoRequest == null) {
            return null;
        }
        return User.builder()
                .name(userDtoRequest.getName())
                .email(userDtoRequest.getEmail())
                .build();
    }

}
