package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
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

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemServiceUnitTest {

    public static final Sort SORT_START_DESC = Sort.by(Sort.Direction.DESC, "start");
    public static final Sort SORT_START_ASC = Sort.by(Sort.Direction.ASC, "start");
    @InjectMocks
    ItemServiceImpl itemService;
    @Mock
    UserRepository userRepository;
    @Mock
    ItemRepository itemRepository;
    @Mock
    BookingRepository bookingRepository;
    @Mock
    ItemRequestRepository itemRequestRepository;
    @Mock
    CommentRepository commentRepository;
    LocalDateTime now;
    Long invalidId;
    Long ownerId;
    User owner;
    Long requesterId;
    User requester;
    Long requestId;
    ItemRequest request;

    @BeforeEach
    void setUp() {

        now = LocalDateTime.now();
        invalidId = 111L;

        ownerId = 1L;
        owner = User.builder()
                .id(ownerId)
                .name("owner")
                .email("owner@gmail.com")
                .build();

        requesterId = 2L;
        requester = User.builder()
                .id(requesterId)
                .name("requester")
                .email("requester@gmail.com")
                .build();

        requestId = 1L;
        request = ItemRequest.builder()
                .id(requestId)
                .description("request")
                .created(now)
                .requester(requester)
                .build();

    }

    @Test
    void createItem_Success() {

        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        when(itemRequestRepository.findById(requestId)).thenReturn(Optional.of(request));

        ItemDtoRequest itemDtoRequest = ItemDtoRequest.builder()
                .name("drill")
                .description("drill for drilling")
                .available(true)
                .requestId(requestId)
                .build();
        Item item = ItemMapper.toItem(itemDtoRequest);
        item.setId(1L);
        item.setRequest(request);
        item.setOwner(owner);
        ItemDtoResponse itemDtoResponse = ItemMapper.toItemDtoResponse(item);
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        ItemDtoResponse actualItemDto = itemService.createItem(ownerId, itemDtoRequest);

        Assertions.assertNotNull(actualItemDto.getId());
        Assertions.assertEquals(itemDtoResponse.getName(), actualItemDto.getName());
        Assertions.assertEquals(itemDtoResponse.getDescription(), actualItemDto.getDescription());
        Assertions.assertEquals(itemDtoResponse.getAvailable(), actualItemDto.getAvailable());
        Assertions.assertEquals(itemDtoResponse.getRequestId(), actualItemDto.getRequestId());

    }

    @Test
    void createItem_ItemRequestNotFound() {

        when(itemRequestRepository.findById(invalidId)).thenReturn(Optional.empty());
        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));

        ItemDtoRequest itemDtoRequest = ItemDtoRequest.builder()
                .name("drill")
                .description("drill for drilling")
                .available(true)
                .requestId(invalidId)
                .build();

        DataNotFoundException thrown = assertThrows(
                DataNotFoundException.class,
                () -> itemService.createItem(ownerId, itemDtoRequest),
                "Expected createItem() to throw, but it did not"
        );

        assertTrue(thrown.getMessage().contains("ItemRequest not found"));

    }

    @Test
    void createItem_UserNotFound() {

        when(userRepository.findById(invalidId)).thenReturn(Optional.empty());

        ItemDtoRequest itemDtoRequest = ItemDtoRequest.builder()
                .name("drill")
                .description("drill for drilling")
                .available(true)
                .requestId(invalidId)
                .build();

        DataNotFoundException thrown = assertThrows(
                DataNotFoundException.class,
                () -> itemService.createItem(invalidId, itemDtoRequest),
                "Expected createItem() to throw, but it did not"
        );

        assertTrue(thrown.getMessage().contains("User not found"));

    }


    @Test
    void createComment() {

        User author = User.builder()
                .id(2L)
                .name("author")
                .email("author@gmail.com")
                .build();

        Item item = Item.builder()
                .id(1L)
                .name("Shovel")
                .owner(owner)
                .description("Shovel for digging what is digging")
                .available(true)
                .build();

        Comment comment = Comment.builder()
                .item(item)
                .author(author)
                .text("comment text")
                .created(LocalDateTime.of(2000, 12, 12, 12, 12))
                .build();

        Comment commentSaved = Comment.builder()
                .id(1L)
                .item(item)
                .author(author)
                .text("comment text")
                .created(LocalDateTime.of(2000, 12, 12, 12, 12))
                .build();

        CommentDtoResponse commentDto = CommentDtoResponse.builder()
                .id(1L)
                .text("comment text")
                .authorName(author.getName())
                .created(comment.getCreated())
                .build();

        CommentDtoRequest commentRequestDto = CommentDtoRequest.builder()
                .text("comment text")
                .build();

        Booking newBooking = Booking.builder()
                .status(BookingStatus.APPROVED)
                .item(item)
                .start(now.minusDays(4))
                .end(now.minusDays(2))
                .booker(author)
                .build();


        CommentDtoResponse expectedComment;
        CommentDtoResponse actualComment;

        Mockito
                .when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(author));
        Mockito
                .when(bookingRepository.findAllByItemAndBooker(any(), any()))
                .thenReturn(List.of(newBooking));
        Mockito
                .when(commentRepository.save(any(Comment.class)))
                .thenReturn(commentSaved);

        expectedComment = commentDto;
        actualComment = itemService.createComment(author.getId(), item.getId(), commentRequestDto);

        Assertions.assertEquals(expectedComment, actualComment);

    }

    @Test
    void updateItemDtoRequest() {

        Long itemId = 1L;
        ItemDtoRequest itemRequestDto = ItemDtoRequest.builder()
                .name("Updated Item Name")
                .description("Updated Description")
                .available(true)
                .build();
        long userId = 1L;

        User user = new User();
        user.setId(userId);

        Item item = Item.builder()
                .id(itemId)
                .name("Initial Item Name")
                .description("Initial Description")
                .available(false)
                .owner(user)
                .build();

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(itemRepository.findById(itemId)).willReturn(Optional.of(item));

        given(itemRepository.save(any(Item.class))).willReturn(item);

        ItemDtoResponse updatedItem = itemService.update(userId, itemId, itemRequestDto);

        assertEquals(itemRequestDto.getName(), updatedItem.getName());
        assertEquals(itemRequestDto.getDescription(), updatedItem.getDescription());
        assertEquals(itemRequestDto.getAvailable(), updatedItem.getAvailable());

    }

    @Test
    void getItemDtoResponse() {

        owner = User.builder()
                .id(1L)
                .name("user")
                .email("email@gmail.com")
                .build();

        requester = User.builder()
                .id(2L)
                .name("user2")
                .email("email2@gmail.com")
                .build();

        Item itemSaved = Item.builder()
                .id(1L)
                .name("Shovel")
                .owner(owner)
                .description("Shovel for digging what is digging")
                .available(true)
                .build();

        Booking lastBooking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now().minusDays(4))
                .end(LocalDateTime.now().minusDays(3))
                .booker(requester)
                .item(itemSaved)
                .status(BookingStatus.APPROVED)
                .build();

        Booking nextBooking = Booking.builder()
                .id(2L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .booker(requester)
                .item(itemSaved)
                .status(BookingStatus.APPROVED)
                .build();

        ItemDtoResponseWithBookingDates itemDtoWithBookings = ItemDtoResponseWithBookingDates.builder()
                .id(1L)
                .name("Shovel")
                .description("Shovel for digging what is digging")
                .available(true)
                .lastBooking(BookingMapper.toBookingDtoResponseForItem(lastBooking))
                .nextBooking(BookingMapper.toBookingDtoResponseForItem(nextBooking))
                .build();

        ItemDtoResponseWithBookingDates expectedDto;
        ItemDtoResponseWithBookingDates actualDto;

        itemDtoWithBookings.setComments(Collections.emptyList());
        Mockito
                .when(itemRepository.findById(itemSaved.getId()))
                .thenReturn(Optional.ofNullable(itemSaved));
        Mockito
                .when(bookingRepository.findFirstByItemAndStatusIsNotAndStartBefore(
                        Mockito.eq(itemSaved),
                        Mockito.eq(BookingStatus.REJECTED),
                        Mockito.any(LocalDateTime.class),
                        Mockito.eq(SORT_START_DESC)))
                .thenReturn(Optional.of(lastBooking));
        Mockito
                .when(bookingRepository.findFirstByItemAndStatusIsNotAndStartAfter(
                        Mockito.eq(itemSaved),
                        Mockito.eq(BookingStatus.REJECTED),
                        Mockito.any(LocalDateTime.class),
                        Mockito.eq(SORT_START_ASC)))
                .thenReturn(Optional.of(nextBooking));
        Mockito
                .when(commentRepository.findAllByItem(itemSaved))
                .thenReturn(Collections.emptyList());


        expectedDto = itemDtoWithBookings;
        actualDto = itemService.getItemDtoResponse(itemSaved.getId(), owner.getId());

        Assertions.assertEquals(expectedDto, actualDto);
    }

    @Test
    public void testGetItemsBySearch_WhenTextIsNotBlank() {

        String searchText = "search text";
        Item item1 = new Item();
        item1.setId(1L);
        item1.setName("Test Item 1");
        item1.setDescription("Description 1");

        Item item2 = new Item();
        item2.setId(2L);
        item2.setName("Test Item 2");
        item2.setDescription("Description 2");

        List<Item> itemList = Arrays.asList(item1, item2);

        Pageable page = PageRequest.of(0, 10);
        Mockito.when(itemRepository.searchByText(searchText, page)).thenReturn(itemList);

        List<ItemDtoResponse> result = itemService.searchByText(searchText, 0, 10);

        assertNotNull(result);
        assertEquals(2, result.size());

    }

    @Test
    public void testGetItemsBySearch_TextIsBlank() {

        String searchText = "";

        List<ItemDtoResponse> result = itemService.searchByText(searchText, 0, 10);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

}