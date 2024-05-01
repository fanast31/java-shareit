package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.user.dto.UserDtoResponse;

import java.time.LocalDateTime;

@Data
@Builder
public class ItemRequestDtoResponse {

    private Long id;

    private String description;

    private UserDtoResponse requester;

    private LocalDateTime created;

}
