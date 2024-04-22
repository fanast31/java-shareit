package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ItemRepositoryImpl implements ItemRepository{
    private long id = 1L;
    private final Map<Long, Item> items = new HashMap<>();

    @Override
    public Item findById(long itemId) {
        return null;
    }

    @Override
    public List<Item> getAll() {
        return null;
    }

    @Override
    public Item save(Item item) {
        return null;
    }

    @Override
    public void delete(long itemId) {
        items.remove(itemId);
    }
}
