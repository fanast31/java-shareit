package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    ItemDtoResponse createItem(long userId, ItemDtoRequest itemDtoRequest);

    CommentDtoResponse createComment(long userId, long itemId, CommentDtoRequest commentDtoRequest);

    ItemDtoResponse update(long userId, long itemId, ItemDtoRequest itemDtoRequest);

    Item getItem(long itemId);

    ItemDtoResponseWithBookingDates getItemDtoResponse(long itemId, long userId);

    List<ItemDtoResponseWithBookingDates> getAll(long userId);

    List<ItemDtoResponse> searchByText(String searchText);

}
