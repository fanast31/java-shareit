package ru.practicum.shareit.item;

import java.util.List;

public interface ItemRepository {
    Item findById(long itemId);
    List<Item> getAll();
    Item save(Item item);
    void delete(long itemId);
}
