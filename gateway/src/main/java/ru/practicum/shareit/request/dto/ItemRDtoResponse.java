package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemDtoResponse;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ItemRDtoResponse {

    private Long id;

    private String description;

    private List<ItemDtoResponse> items;

    private LocalDateTime created;

}
