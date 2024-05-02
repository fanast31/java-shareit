package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.DataNotFoundException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;

    private final UserRepository userRepository;

    private final BookingRepository bookingRepository;

    private final CommentRepository commentRepository;

    @Override
    public ItemDtoResponse createItem(long userId, ItemDtoRequest itemDtoRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("User not found"));
        Item item = ItemMapper.toItem(itemDtoRequest);
        item.setOwner(user);
        return ItemMapper.toItemDtoResponse(itemRepository.save(item));
    }

    @Override
    public CommentDtoResponse createComment(long userId, long itemId, CommentDtoRequest commentDtoRequest) {

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new DataNotFoundException("Item not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("User not found"));

        LocalDateTime now = LocalDateTime.now();
        if (bookingRepository.findAllByItemAndBooker(item, user).stream().noneMatch(b -> b.isFinished(now))) {
            throw new BadRequestException("No finished bookings for this user and thi item were found");
        }
        ;

        Comment comment = CommentMapper.toComment(commentDtoRequest);
        comment.setCreated(now);
        comment.setItem(item);
        comment.setAuthor(user);

        return CommentMapper.toCommentDtoResponse(commentRepository.save(comment));

    }

    @Override
    public ItemDtoResponse update(long userId, long itemId, ItemDtoRequest itemDtoRequest) {

        User userDB = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("User not found"));
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
                .map(item -> addNecessaryFields(item, userId))
                .orElseThrow(() -> new DataNotFoundException("Item not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDtoResponseWithBookingDates> getAll(long userId) {
        return itemRepository.findAllByOwnerId(userId).stream()
                .map(item -> addNecessaryFields(item, userId))
                .collect(Collectors.toList());
    }

    private ItemDtoResponseWithBookingDates addNecessaryFields(Item item, Long userId) {

        ItemDtoResponseWithBookingDates newItem = ItemMapper.toItemDtoResponseWithBookingDates(item);

        if (Objects.equals(item.getOwner().getId(), userId)) {
            LocalDateTime start = LocalDateTime.now();
            final Booking lastBooking = bookingRepository
                    .findFirstByItemAndStatusIsNotAndStartBeforeOrderByStartDesc(item, BookingStatus.REJECTED, start)
                    .orElse(null);
            final Booking nextBooking = bookingRepository
                    .findFirstByItemAndStatusIsNotAndStartAfterOrderByStart(item, BookingStatus.REJECTED, start)
                    .orElse(null);

            newItem.setLastBooking(BookingMapper.toBookingDtoResponseForItem(lastBooking));
            newItem.setNextBooking(BookingMapper.toBookingDtoResponseForItem(nextBooking));
        }

        newItem.setComments(commentRepository.findAllByItem(item).stream()
                .map(CommentMapper::toCommentDtoResponse)
                .collect(Collectors.toList()));

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
