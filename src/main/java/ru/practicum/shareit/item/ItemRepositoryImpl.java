package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ItemRepositoryImpl implements ItemRepository {
    private long id = 1L;
    private final Map<Long, Item> items = new HashMap<>();

    @Override
    public Item findById(long itemId) {
        return items.get(itemId);
    }

    @Override
    public List<Item> getAll(long userId) {
        return items.values().stream()
                .filter(item -> item.getOwner() != null
                        && item.getOwner().getId() == userId).collect(Collectors.toList());
    }

    @Override
    public Item save(Item item) {
        if (item.getId() == null) {
            item.setId(id++);
        }
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public void delete(long itemId) {
        items.remove(itemId);
    }

    @Override
    public List<Item> searchByText(String searchText) {

        String finalSearchText = searchText.toLowerCase();

        return items.values().stream()
                .filter(item -> item.getName().toLowerCase().contains(finalSearchText)
                        || item.getDescription().toLowerCase().contains(finalSearchText))
                .filter(Item::getAvailable)
                .collect(Collectors.toList());
    }
}
