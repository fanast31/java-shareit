package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.DataNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;

    private final UserService userService;

    @Override
    public Item create(long userId, Item item) {
        User user = userService.get(userId);
        item.setOwner(user);
        return itemRepository.save(item);
    }

    @Override
    public Item update(long userId, long itemId, Item item) {

        User userDB = userService.get(userId);
        Item itemDB = itemRepository.findById(itemId);
        if (itemDB == null) {
            throw new DataNotFoundException("Item not found");
        }
        if (!itemDB.getOwner().equals(userDB)) {
            throw new DataNotFoundException("The item can only be changed by the owner");
        }

        if (item.getName() != null) {
            itemDB.setName(item.getName());
        }
        if (item.getDescription() != null) {
            itemDB.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            itemDB.setAvailable(item.getAvailable());
        }

        itemRepository.save(itemDB);
        return itemDB;
    }

    @Override
    public Item get(long itemId) {
        return itemRepository.findById(itemId);
    }

    @Override
    public List<Item> getAll(long userId) {
        return itemRepository.getAll(userId);
    }

    @Override
    public void delete(long itemId) {
        itemRepository.delete(itemId);
    }

    @Override
    public List<Item> searchByText(String searchText) {
        return itemRepository.searchByText(searchText);
    }
}
