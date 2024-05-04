package ru.practicum.shareit.user.dto;

import lombok.*;

@Data
@Builder
public class UserDtoResponse {

    private Long id;

    private String name;

    private String email;

}
