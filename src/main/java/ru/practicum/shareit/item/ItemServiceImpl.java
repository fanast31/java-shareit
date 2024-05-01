package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.DataNotFoundException;
import ru.practicum.shareit.item.dto.ItemDtoRequest;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;

    private final UserService userService;

    @Override
    public ItemDtoResponse create(long userId, ItemDtoRequest itemDtoRequest) {
        User user = userService.getUser(userId);
        Item item = ItemMapper.toItem(itemDtoRequest);
        item.setOwner(user);
        return ItemMapper.toItemDtoResponse(itemRepository.save(item));
    }

    @Override
    public ItemDtoResponse update(long userId, long itemId, ItemDtoRequest itemDtoRequest) {

        User userDB = userService.getUser(userId);
        Item itemDB = itemRepository.findById(itemId)
                .orElseThrow(() -> new DataNotFoundException("Item not found"));
        if (!itemDB.getOwner().equals(userDB)) {
            throw new DataNotFoundException("The item can only be changed by the owner");
        }

        boolean changed = false;
        if (itemDtoRequest.getName() != null) {
            itemDB.setName(itemDtoRequest.getName());
            changed = true;
        }
        if (itemDtoRequest.getDescription() != null) {
            itemDB.setDescription(itemDtoRequest.getDescription());
            changed = true;
        }
        if (itemDtoRequest.getAvailable() != null) {
            itemDB.setAvailable(itemDtoRequest.getAvailable());
            changed = true;
        }

        if (changed) {
            itemRepository.save(itemDB);
        }

        return ItemMapper.toItemDtoResponse(itemDB);
    }

    @Override
    @Transactional(readOnly = true)
    public Item getItem(long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new DataNotFoundException("Item not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public ItemDtoResponse getItemDtoResponse(long itemId) {
        return ItemMapper.toItemDtoResponse(getItem(itemId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDtoResponse> getAll(long userId) {
        return itemRepository.findAllByOwnerId(userId).stream()
                .map(ItemMapper::toItemDtoResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDtoResponse> searchByText(String searchText) {
        return itemRepository.searchByText(searchText).stream()
                .map(ItemMapper::toItemDtoResponse)
                .collect(Collectors.toList());
    }
}
