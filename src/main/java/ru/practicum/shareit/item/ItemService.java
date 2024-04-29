package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto create(long userId, ItemDto itemDto);

    ItemDto update(long userId, long itemId, ItemDto itemDto);

    ItemDto get(long itemId);

    List<ItemDto> getAll(long userId);

    void delete(long itemId);

    List<ItemDto> searchByText(String searchText);

}
