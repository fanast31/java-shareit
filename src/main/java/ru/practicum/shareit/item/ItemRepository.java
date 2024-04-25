package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {
    Item findById(long itemId);

    List<Item> getAll(long userId);

    Item save(Item item);

    void delete(long itemId);

    List<Item> searchByText(String searchText);
}
