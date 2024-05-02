package ru.practicum.shareit.request;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.request.dto.ItemRequestDtoRequest;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserMapper;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemRequestMapper {

    public static ItemRequest toItemRequest(ItemRequestDtoRequest itemRequestDtoRequest) {
        if (itemRequestDtoRequest == null) {
            return null;
        }
        return ItemRequest.builder()
                .id(itemRequestDtoRequest.getId())
                .description(itemRequestDtoRequest.getDescription())
                .created(itemRequestDtoRequest.getCreated())
                .build();
    }

    public static ItemRequestDtoResponse toItemRequestDto(ItemRequest itemRequest) {
        if (itemRequest == null) {
            return null;
        }
        return ItemRequestDtoResponse.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .requester(UserMapper.toUserDtoResponse(itemRequest.getRequester()))
                .created(itemRequest.getCreated())
                .build();
    }

}
