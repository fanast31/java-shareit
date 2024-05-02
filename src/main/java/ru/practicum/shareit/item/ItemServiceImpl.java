package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exceptions.DataNotFoundException;
import ru.practicum.shareit.item.dto.ItemDtoRequest;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.dto.ItemDtoResponseWithBookingDates;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;

    private final UserService userService;

    private final BookingRepository bookingServiceRepository;

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
    public ItemDtoResponseWithBookingDates getItemDtoResponse(long itemId, long userId) {
        return itemRepository.findById(itemId)
                .map(item -> AddNecessaryFields(item, userId))
                .orElseThrow(() -> new DataNotFoundException("Item not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDtoResponseWithBookingDates> getAll(long userId) {
        return itemRepository.findAllByOwnerId(userId).stream()
                .map(item -> AddNecessaryFields(item, userId))
                .collect(Collectors.toList());
    }

    private ItemDtoResponseWithBookingDates AddNecessaryFields(Item item, Long userId) {

        ItemDtoResponseWithBookingDates newItem = ItemMapper.toItemDtoResponseWithBookingDates(item);

        if (Objects.equals(item.getOwner().getId(), userId)) {
            LocalDateTime start = LocalDateTime.now();
            final Booking lastBooking = bookingServiceRepository
                    .findFirstByItemAndStartBeforeOrderByStartDesc(item, start)
                    .orElse(null);
            final Booking nextBooking = bookingServiceRepository
                    .findFirstByItemAndStartAfterOrderByStart(item, start)
                    .orElse(null);

            newItem.setLastBooking(BookingMapper.toBookingDtoResponseForItem(lastBooking));
            newItem.setNextBooking(BookingMapper.toBookingDtoResponseForItem(nextBooking));
        }
//        item.setComments(commentRepository.findAllByItem(item).stream()
//                        .map(mapper::to Item Comment)Stream < Item Comment >
//                .collect(Collectors.toList()));
        return newItem;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDtoResponse> searchByText(String searchText) {
        return itemRepository.searchByText(searchText).stream()
                .map(ItemMapper::toItemDtoResponse)
                .collect(Collectors.toList());
    }

}
