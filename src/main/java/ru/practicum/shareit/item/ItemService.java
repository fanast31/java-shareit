package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    Item create(long userId, Item item);

    Item update(long userId, long itemId, Item item);

    Item get(long itemId);

    List<Item> getAll(long userId);

    void delete(long itemId);

    List<Item> searchByText(String searchText);
}
