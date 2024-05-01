package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDtoRequest;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.dto.ItemDtoResponseWithBookingDates;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    ItemDtoResponse create(long userId, ItemDtoRequest itemDtoRequest);

    ItemDtoResponse update(long userId, long itemId, ItemDtoRequest itemDtoRequest);

    Item getItem(long itemId);

    ItemDtoResponseWithBookingDates getItemDtoResponse(long itemId);

    List<ItemDtoResponseWithBookingDates> getAll(long userId);

    List<ItemDtoResponse> searchByText(String searchText);

}
