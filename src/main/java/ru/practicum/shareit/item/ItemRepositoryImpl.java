package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ItemRepositoryImpl implements ItemRepository{
    private long id = 1L;
    private final Map<Long, Item> items = new HashMap<>();

    @Override
    public Item findById(long userId, long itemId) {
        Item item = items.get(itemId);
        if (item == null || item.getOwner() == null || item.getOwner().getId() != userId) {
            return null;
        }
        return item;
    }

    @Override
    public List<Item> getAll(long userId) {
        return items.values().stream()
                .filter(item -> item.getOwner() != null
                        && item.getOwner().getId() == userId).collect(Collectors.toList());
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
