package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDtoResponseForItem;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exceptions.DataNotFoundException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemServiceIntegrationTest {

    private final ItemService itemService;

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ItemRequestRepository requestRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    LocalDateTime now;
    Long invalidId;

    User owner;

    User requester;

    ItemRequest request;

    ItemDtoResponse itemDtoResponse;

    @BeforeEach
    void setUp() {

        invalidId = 111L;
        now = LocalDateTime.now();

        owner = User.builder()
                .name("owner")
                .email("owner@gmail.com")
                .build();

        requester = User.builder()
                .name("requester")
                .email("requester@gmail.com")
                .build();

        request = ItemRequest.builder()
                .description("request")
                .created(now)
                .requester(requester)
                .build();

        itemDtoResponse = ItemDtoResponse.builder()
                .name("drill")
                .description("drill for drilling")
                .available(true)
                .build();

    }

    @Test
    void createItem_Success() {

        userRepository.save(owner);
        userRepository.save(requester);
        requestRepository.save(request);

        ItemDtoRequest itemDtoRequest = ItemDtoRequest.builder()
                .name("drill")
                .description("drill for drilling")
                .available(true)
                .requestId(request.getId())
                .build();

        ItemDtoResponse actualItemDto = itemService.createItem(owner.getId(), itemDtoRequest);

        Assertions.assertNotNull(actualItemDto.getId());
        Assertions.assertEquals(itemDtoRequest.getName(), actualItemDto.getName());
        Assertions.assertEquals(itemDtoRequest.getDescription(), actualItemDto.getDescription());
        Assertions.assertEquals(itemDtoRequest.getAvailable(), actualItemDto.getAvailable());
        Assertions.assertEquals(itemDtoRequest.getRequestId(), actualItemDto.getRequestId());

    }

    @Test
    void createItem_ItemRequestNotFound() {

        userRepository.save(owner);
        userRepository.save(requester);

        ItemDtoRequest itemDtoRequest = ItemDtoRequest.builder()
                .name("drill")
                .description("drill for drilling")
                .available(true)
                .requestId(111L)
                .build();

        DataNotFoundException thrown = assertThrows(
                DataNotFoundException.class,
                () -> itemService.createItem(owner.getId(), itemDtoRequest),
                "Expected createItem() to throw, but it did not"
        );

        assertTrue(thrown.getMessage().contains("ItemRequest not found"));

    }

    @Test
    void createItem_UserNotFound() {

        userRepository.save(requester);
        requestRepository.save(request);

        ItemDtoRequest itemDtoRequest = ItemDtoRequest.builder()
                .name("drill")
                .description("drill for drilling")
                .available(true)
                .requestId(request.getId())
                .build();

        DataNotFoundException thrown = assertThrows(
                DataNotFoundException.class,
                () -> itemService.createItem(111L, itemDtoRequest),
                "Expected createItem() to throw, but it did not"
        );

        assertTrue(thrown.getMessage().contains("User not found"));

    }

    @Test
    void updateItem() {

        User ownerSaved = userRepository.save(owner);

        Item trgItem = Item.builder()
                .name("shovel")
                .description("shoveling")
                .owner(ownerSaved)
                .available(true)
                .build();
        Long trgItemId = itemRepository.save(trgItem).getId();

        ItemDtoRequest updatedItemDtoMarker = ItemDtoRequest.builder()
                .name("new shovel name")
                .description("updated shovel description")
                .available(false)
                .requestId(123L)
                .build();

        ItemDtoResponse actualItemDto = itemService.update(ownerSaved.getId(), trgItemId, updatedItemDtoMarker);
        actualItemDto.setRequestId(updatedItemDtoMarker.getRequestId());

        Assertions.assertNotNull(actualItemDto.getId());
        Assertions.assertEquals(updatedItemDtoMarker.getName(), actualItemDto.getName());
        Assertions.assertEquals(updatedItemDtoMarker.getDescription(), actualItemDto.getDescription());
        Assertions.assertEquals(updatedItemDtoMarker.getAvailable(), actualItemDto.getAvailable());
        Assertions.assertEquals(updatedItemDtoMarker.getRequestId(), actualItemDto.getRequestId());

    }

    @Test
    void getItem() {

        User savedOwner = userRepository.save(owner);
        User savedCommenter = userRepository.save(requester);

        Item newItem = Item.builder()
                .available(itemDtoResponse.getAvailable())
                .name(itemDtoResponse.getName())
                .description(itemDtoResponse.getDescription())
                .owner(savedOwner)
                .build();

        Item persistedItem = itemRepository.save(newItem);

        Comment newComment = Comment.builder()
                .author(savedCommenter)
                .text("commenting item")
                .item(persistedItem)
                .created(now)
                .build();

        commentRepository.save(newComment);

        ItemDtoResponseWithBookingDates retrievedItemDto =
                itemService.getItemDtoResponse(persistedItem.getId(), savedOwner.getId());

        Assertions.assertEquals(persistedItem.getId(), retrievedItemDto.getId());
        Assertions.assertEquals(persistedItem.getName(), retrievedItemDto.getName());

    }

    @Test
    void findAllByOwnerId() {

        List<ItemDtoResponseWithBookingDates> actualDtoListWithDates;

        User savedOwner = userRepository.save(owner);
        User savedBooker = userRepository.save(requester);
        Item drill = Item.builder()
                .name("drill")
                .description("drilling drill")
                .owner(savedOwner)
                .available(true)
                .build();
        Item savedDrill = itemRepository.save(drill);
        Item shovel = Item.builder()
                .name("shovel")
                .description("shovelling shovel")
                .owner(savedOwner)
                .available(true)
                .build();
        Item savedShovel = itemRepository.save(shovel);

        Booking pastBookingDrill = Booking.builder()
                .item(savedDrill)
                .start(now.minusDays(10))
                .end(now.minusDays(9))
                .status(BookingStatus.APPROVED)
                .booker(savedBooker)
                .build();
        Booking pastBookingShovel = Booking.builder()
                .item(savedShovel)
                .start(now.minusDays(2))
                .end(now.minusDays(1))
                .status(BookingStatus.APPROVED)
                .booker(savedBooker)
                .build();
        Booking futureBookingDrill = Booking.builder()
                .item(savedDrill)
                .start(now.plusDays(9))
                .end(now.plusDays(10))
                .status(BookingStatus.APPROVED)
                .booker(savedBooker)
                .build();
        Booking futureBookingShovel = Booking.builder()
                .item(savedShovel)
                .start(now.plusDays(1))
                .end(now.plusDays(2))
                .status(BookingStatus.APPROVED)
                .booker(savedBooker)
                .build();

        BookingDtoResponseForItem pastBookingDrillDto = BookingMapper.toBookingDtoResponseForItem(bookingRepository.save(pastBookingDrill));
        BookingDtoResponseForItem pastBookingShovelDto = BookingMapper.toBookingDtoResponseForItem(bookingRepository.save(pastBookingShovel));
        BookingDtoResponseForItem futureBookingDrillDto = BookingMapper.toBookingDtoResponseForItem(bookingRepository.save(futureBookingDrill));
        BookingDtoResponseForItem futureBookingShovelDto = BookingMapper.toBookingDtoResponseForItem(bookingRepository.save(futureBookingShovel));

        actualDtoListWithDates = itemService.getAll(savedOwner.getId(), 0, 10);

        Assertions.assertEquals(pastBookingDrillDto, actualDtoListWithDates.get(0).getLastBooking());
        Assertions.assertEquals(pastBookingShovelDto, actualDtoListWithDates.get(1).getLastBooking());
        Assertions.assertEquals(futureBookingDrillDto, actualDtoListWithDates.get(0).getNextBooking());
        Assertions.assertEquals(futureBookingShovelDto, actualDtoListWithDates.get(1).getNextBooking());

    }

    @Test
    void findItem() {

        User savedOwner = userRepository.save(owner);

        Item newItem = Item.builder()
                .name(itemDtoResponse.getName())
                .owner(savedOwner)
                .description(itemDtoResponse.getDescription())
                .available(itemDtoResponse.getAvailable())
                .build();

        Item savedNewItem = itemRepository.save(newItem);

        List<ItemDtoResponse> listExpectedDto = List.of(ItemMapper.toItemDtoResponse(savedNewItem));
        List<ItemDtoResponse> listActualDto = itemService.searchByText("drill", 0, 10);

        Assertions.assertEquals(listExpectedDto, listActualDto);

    }

    @Test
    void createComment() {

        User savedOwner = userRepository.save(owner);
        User savedBooker = userRepository.save(requester);

        Item newItem = Item.builder()
                .available(itemDtoResponse.getAvailable())
                .owner(savedOwner)
                .name(itemDtoResponse.getName())
                .description(itemDtoResponse.getDescription())
                .build();
        Item savedItem = itemRepository.save(newItem);

        Booking newBooking = Booking.builder()
                .status(BookingStatus.APPROVED)
                .item(savedItem)
                .start(now.minusDays(4))
                .end(now.minusDays(2))
                .booker(savedBooker)
                .build();
        bookingRepository.save(newBooking);

        CommentDtoRequest newCommentRequest = CommentDtoRequest.builder()
                .text("text text")
                .build();

        CommentDtoResponse responseCommentDto = itemService.createComment(savedBooker.getId(), savedItem.getId(), newCommentRequest);

        Assertions.assertEquals(1L, responseCommentDto.getId());
        Assertions.assertEquals(newCommentRequest.getText(), responseCommentDto.getText());
        Assertions.assertEquals(savedBooker.getName(), responseCommentDto.getAuthorName());

    }

}
