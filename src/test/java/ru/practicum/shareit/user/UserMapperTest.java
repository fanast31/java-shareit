package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.UserDtoRequest;
import ru.practicum.shareit.user.dto.UserDtoResponse;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class UserMapperTest {

    @Test
    public void toUser_withValidUserDtoRequest_returnsUser() {

        UserDtoRequest userDtoRequest = UserDtoRequest.builder()
                .name("Jane")
                .email("jane@example.com")
                .build();

        User result = UserMapper.toUser(userDtoRequest);

        assertEquals("Jane", result.getName());
        assertEquals("jane@example.com", result.getEmail());

    }

    @Test
    public void toUser_withNullUserDtoRequest_returnsNull() {

        User result = UserMapper.toUser(null);

        assertNull(result);

    }

    @Test
    public void toUserDtoResponse_withValidUser_returnsUserDtoResponse() {

        User user = User.builder()
                .id(1L)
                .name("Jane")
                .email("jane@example.com")
                .build();

        UserDtoResponse result = UserMapper.toUserDtoResponse(user);

        assertEquals(1L, result.getId());
        assertEquals("Jane", result.getName());
        assertEquals("jane@example.com", result.getEmail());
    }

    @Test
    public void toUserDtoResponse_withNullUser_returnsNull() {

        UserDtoResponse result = UserMapper.toUserDtoResponse(null);

        assertNull(result);

    }
}