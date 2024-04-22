package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.DataNotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService{

    private final ItemRepository itemRepository;

    private final UserRepository userRepository;

    @Override
    public Item create(long userId, Item item) {
        User user = userRepository.findById(userId);
        if (user == null) {
            throw new DataNotFoundException("User not found");
        }
        item.setOwner(user);
        return itemRepository.save(item);
    }

    @Override
    public Item update(long itemId, Item item) {
        return null;
    }

    @Override
    public Item get(long itemId) {
        return null;
    }

    @Override
    public List<Item> getAll() {
        return null;
    }

    @Override
    public void delete(long itemId) {

    }
}
