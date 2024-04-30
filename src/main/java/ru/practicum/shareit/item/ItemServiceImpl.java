package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.DataNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
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
    public ItemDto create(long userId, ItemDto itemDto) {
        User user = userService.getUser(userId);
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(user);
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto update(long userId, long itemId, ItemDto itemDto) {

        User userDB = userService.getUser(userId);
        Item itemDB = itemRepository.findById(itemId)
                .orElseThrow(() -> new DataNotFoundException("Item not found"));
        if (!itemDB.getOwner().equals(userDB)) {
            throw new DataNotFoundException("The item can only be changed by the owner");
        }

        if (itemDto.getName() != null) {
            itemDB.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            itemDB.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            itemDB.setAvailable(itemDto.getAvailable());
        }

        itemRepository.save(itemDB);
        return ItemMapper.toItemDto(itemDB);
    }

    @Override
    @Transactional(readOnly = true)
    public Item getItem(long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new DataNotFoundException("Item not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public ItemDto getItemDto(long itemId) {
        return ItemMapper.toItemDto(getItem(itemId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> getAll(long userId) {
        return itemRepository.findAllByOwnerId(userId).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> searchByText(String searchText) {
        return itemRepository.searchByText(searchText).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }
}
