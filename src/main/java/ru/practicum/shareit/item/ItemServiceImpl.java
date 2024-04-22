package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.DataNotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService{

    private final ItemRepository itemRepository;

    private final UserRepository userRepository;

    @Override
    public Item create(long userId, Item item) throws DataNotFoundException {
        User user = userRepository.findById(userId);
        if (user == null) {
            throw new DataNotFoundException("User not found");
        }
        item.setOwner(user);
        return itemRepository.save(item);
    }

    @Override
    public Item update(long userId, long itemId, Item item) throws DataNotFoundException, ValidationException{

        User userDB = userRepository.findById(userId);
        if (userDB == null) {
            throw new DataNotFoundException("User not found");
        }
        Item itemDB = itemRepository.findById(itemId);
        if (itemDB == null) {
            throw new DataNotFoundException("Item not found");
        }
        if (!itemDB.getOwner().equals(userDB)) {
            throw new ValidationException("The item can only be changed by the owner");
        }

        Item newItem = new Item(
                itemDB.getId(),
                itemDB.getName(),
                itemDB.getDescription(),
                itemDB.getAvailable(),
                itemDB.getOwner(),
                itemDB.getRequest());
        if (item.getName() != null) {
            newItem.setName(item.getName());
        }
        if (item.getDescription() != null) {
            newItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            newItem.setAvailable(item.getAvailable());
        }

        itemRepository.save(newItem);
        return newItem;
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
}
