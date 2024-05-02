package ru.practicum.shareit.item.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
public class CommentDtoResponse {

    private Long id;

    private String text;

    private String authorName;

    private LocalDateTime created;

}