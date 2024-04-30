package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    ItemDto create(long userId, ItemDto itemDto);

    ItemDto update(long userId, long itemId, ItemDto itemDto);

    Item getItem(long itemId);

    ItemDto getItemDto(long itemId);

    List<ItemDto> getAll(long userId);

    List<ItemDto> searchByText(String searchText);

}
