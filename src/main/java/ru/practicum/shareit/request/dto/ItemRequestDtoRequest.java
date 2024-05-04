package ru.practicum.shareit.request.dto;

import lombok.Data;
import ru.practicum.shareit.user.dto.UserDtoRequest;

import java.time.LocalDateTime;

@Data
public class ItemRequestDtoRequest {

    private Long id;

    private String description;

    private UserDtoRequest requester;

    private LocalDateTime created;
}
