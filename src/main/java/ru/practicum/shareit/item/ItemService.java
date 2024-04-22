package ru.practicum.shareit.item;

import java.util.List;

public interface ItemService {
    Item create(long userId, Item item);
    Item update(long itemId, Item item);
    Item get(long itemId);
    List<Item> getAll();
    void delete(long itemId);
}
