package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ItemRepositoryImpl implements ItemRepository{
    private long id = 1L;
    private final Map<Long, Item> items = new HashMap<>();

    @Override
    public Item findById(long itemId) {
        return items.get(itemId);
    }

    @Override
    public List<Item> getAll() {
        return new ArrayList<>(items.values());
    }

    @Override
    public Item save(Item item) {
        if(item.getId() == null) {
            item.setId(id++);
        }
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public void delete(long itemId) {
        items.remove(itemId);
    }
}
