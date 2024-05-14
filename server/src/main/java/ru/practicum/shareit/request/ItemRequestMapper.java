package ru.practicum.shareit.request;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.request.dto.ItemRDtoRequest;
import ru.practicum.shareit.request.dto.ItemRDtoResponse;
import ru.practicum.shareit.request.model.ItemRequest;

@UtilityClass
public class ItemRequestMapper {

    public static ItemRequest toItemRequest(ItemRDtoRequest itemRequestDtoRequest) {
        if (itemRequestDtoRequest == null) {
            return null;
        }
        return ItemRequest.builder()
                .description(itemRequestDtoRequest.getDescription())
                .build();
    }

    public static ItemRDtoResponse toItemRDtoResponse(ItemRequest itemRequest) {
        if (itemRequest == null) {
            return null;
        }
        return ItemRDtoResponse.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .build();
    }

}
