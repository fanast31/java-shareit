package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.DataNotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.utils.PaginationUtils;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceUnitTest {
    @InjectMocks
    BookingServiceImpl bookingService;
    @Mock
    BookingRepository bookingRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    ItemRepository itemRepository;
    Item item;
    Item item2;
    Item item3;
    User owner;
    User owner2;
    User booker;
    User booker23;
    Booking booking;
    Booking booking2;
    Booking booking3;
    LocalDateTime now;
    BookingDtoResponse bookingDtoResponse;
    BookingDtoRequest bookingDtoRequest;
    Long ownerId;
    Long ownerId2;
    Long bookerId;
    Long bookerId23;
    Long itemId;
    Long itemId2;
    Long bookingId;
    Long bookingId2;

    @BeforeEach
    void setUp() {

        now = LocalDateTime.now();

        ownerId = 1L;
        owner = User.builder()
                .id(ownerId)
                .name("owner")
                .email("owner@gmail.com")
                .build();

        bookerId = 2L;
        booker = User.builder()
                .id(bookerId)
                .name("booker")
                .email("booker@gmail.com")
                .build();

        itemId = 1L;
        item = Item.builder()
                .id(itemId)
                .name("item")
                .description("item")
                .owner(owner)
                .available(true)
                .request(null)
                .build();

        bookingId = 1L;
        booking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now().minusDays(1))
                .end(LocalDateTime.now().plusDays(1))
                .item(item)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build();

        bookingDtoRequest = BookingDtoRequest.builder()
                .itemId(itemId)
                .start(booking.getStart())
                .end(booking.getEnd())
                .build();

        bookingDtoResponse = BookingDtoResponse.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(ItemMapper.toItemDtoResponse(booking.getItem()))
                .booker(UserMapper.toUserDtoResponse(booking.getBooker()))
                .status(BookingStatus.APPROVED)
                .build();

        ownerId2 = 3L;
        owner2 = User.builder()
                .id(ownerId2)
                .name("owner2")
                .email("owner2@gmail.com")
                .build();
        bookerId23 = 4L;
        booker23 = User.builder()
                .id(bookerId23)
                .name("booker2")
                .email("booker2@gmail.com")
                .build();

        itemId2 = 2L;
        item2 = Item.builder()
                .id(itemId2)
                .name("item2")
                .description("item2")
                .owner(owner2)
                .available(true)
                .request(null)
                .build();
        bookingId2 = 2L;
        booking2 = Booking.builder()
                .id(bookingId2)
                .start(LocalDateTime.now().minusDays(4))
                .end(LocalDateTime.now().minusDays(3))
                .item(item2)
                .booker(booker23)
                .status(BookingStatus.APPROVED)
                .build();
        item3 = Item.builder()
                .id(itemId2 + 1)
                .owner(owner2)
                .build();
        booking3 = Booking.builder()
                .id(bookingId2 + 1)
                .start(LocalDateTime.now().minusDays(6))
                .end(LocalDateTime.now().minusDays(5))
                .item(item3)
                .booker(booker23)
                .status(BookingStatus.APPROVED)
                .build();

    }

    @Test
    void createBooking_Successful() {

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(userRepository.findById(bookerId)).thenReturn(Optional.of(booker));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingDtoResponse result = bookingService.createBooking(bookerId, bookingDtoRequest);

        assertNotNull(result);
        Assertions.assertEquals(bookingDtoResponse.getId(), result.getId());
        Assertions.assertEquals(bookingDtoResponse.getStart(), result.getStart());
        Assertions.assertEquals(bookingDtoResponse.getItem(), result.getItem());
        Assertions.assertEquals(bookingDtoResponse.getBooker(), result.getBooker());
        Assertions.assertEquals(bookingDtoResponse.getStatus(), result.getStatus());

    }

    @Test
    void createBooking_ItemNotFound_ThrowsDataNotFoundException() {

        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());
        assertThrows(DataNotFoundException.class, () -> {
            bookingService.createBooking(bookerId, bookingDtoRequest);
        });
    }

    @Test
    void createBooking_UserNotFound_ThrowsDataNotFoundException() {

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(userRepository.findById(bookerId)).thenReturn(Optional.empty());

        assertThrows(DataNotFoundException.class, () -> {
            bookingService.createBooking(bookerId, bookingDtoRequest);
        });
    }

    @Test
    void createBooking_ItemNotAvailable_ThrowsBadRequestException() {

        item.setAvailable(false);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        assertThrows(BadRequestException.class, () -> {
            bookingService.createBooking(bookerId, bookingDtoRequest);
        });
    }

    @Test
    void createBooking_BookerIsOwner_ThrowsDataNotFoundException() {

        item.setOwner(booker);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(userRepository.findById(bookerId)).thenReturn(Optional.of(booker));

        assertThrows(DataNotFoundException.class, () -> {
            bookingService.createBooking(bookerId, bookingDtoRequest);
        });
    }

    @Test
    void updateBookingStatus_BookingApproved_Success() {

        booking.setStatus(BookingStatus.WAITING);

        when(bookingRepository.findByIdAndItem_Owner_Id(bookingId, ownerId)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingDtoResponse result = bookingService.updateBookingStatus(
                bookingId, ownerId, true);

        assertNotNull(result);
        Assertions.assertEquals(bookingDtoResponse.getId(), result.getId());
        Assertions.assertEquals(bookingDtoResponse.getStart(), result.getStart());
        Assertions.assertEquals(bookingDtoResponse.getItem(), result.getItem());
        Assertions.assertEquals(bookingDtoResponse.getBooker(), result.getBooker());
        Assertions.assertEquals(BookingStatus.APPROVED, result.getStatus());

        result = bookingService.updateBookingStatus(
                bookingId, ownerId, false);
        Assertions.assertEquals(BookingStatus.REJECTED, result.getStatus());

    }

    @Test
    void updateBookingStatus_NotFound_ThrowsDataNotFoundException() {

        when(bookingRepository.findByIdAndItem_Owner_Id(bookingId, ownerId)).thenReturn(Optional.empty());
        assertThrows(DataNotFoundException.class, () -> {
            bookingService.updateBookingStatus(bookingId, ownerId, false);
        });
    }

    @Test
    void updateBookingStatus_AlreadyApproved_ThrowsBadRequestException() {

        booking.setStatus(BookingStatus.APPROVED);

        when(bookingRepository.findByIdAndItem_Owner_Id(bookingId, ownerId)).thenReturn(Optional.of(booking));

        assertThrows(BadRequestException.class, () -> {
            bookingService.updateBookingStatus(bookingId, ownerId, true);
        });
    }

    @Test
    void getBookingById_SuccessfulRetrieval() {

        when(bookingRepository.bookingForView(bookingId, bookerId)).thenReturn(Optional.of(booking));

        BookingDtoResponse result = bookingService.getBookingById(bookingId, bookerId);

        assertNotNull(result);
        Assertions.assertEquals(bookingDtoResponse.getId(), result.getId());
        Assertions.assertEquals(bookingDtoResponse.getStart(), result.getStart());
        Assertions.assertEquals(bookingDtoResponse.getItem(), result.getItem());
        Assertions.assertEquals(bookingDtoResponse.getBooker(), result.getBooker());
        Assertions.assertEquals(bookingDtoResponse.getStatus(), result.getStatus());

        when(bookingRepository.bookingForView(bookingId, ownerId)).thenReturn(Optional.of(booking));
        result = bookingService.getBookingById(bookingId, ownerId);
        assertNotNull(result);
        Assertions.assertEquals(bookingDtoResponse.getId(), result.getId());
        Assertions.assertEquals(bookingDtoResponse.getStart(), result.getStart());
        Assertions.assertEquals(bookingDtoResponse.getItem(), result.getItem());
        Assertions.assertEquals(bookingDtoResponse.getBooker(), result.getBooker());
        Assertions.assertEquals(bookingDtoResponse.getStatus(), result.getStatus());

    }

    @Test
    void getBookingById_NoAccessRights_ThrowsDataNotFoundException() {

        when(bookingRepository.bookingForView(bookingId, bookerId)).thenReturn(Optional.empty());

        assertThrows(DataNotFoundException.class, () -> {
            bookingService.getBookingById(bookingId, bookerId);
        });
    }

    @Test
    void testGetBookingsForCurrentBooker_All() {

        Page<Booking> mockedList = new PageImpl<>(Arrays.asList(booking2, booking3));
        List<BookingDtoResponse> mockedDtoList = Stream.of(booking2, booking3)
                .map(BookingMapper::toBookingDtoResponse)
                .collect(Collectors.toList());

        Integer from = 0;
        Integer size = 10;
        Pageable page = PaginationUtils.createPageable(from, size, PaginationUtils.SORT_START_DESC);
        when(bookingRepository.findByBookerId(bookerId23, page)).thenReturn(mockedList);

        List<BookingDtoResponse> result = bookingService.getBookingsForCurrentBooker(
                bookerId23, BookingState.ALL, 0, 10);


        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(mockedDtoList, result);

    }

    @Test
    void testGetBookingsForCurrentBooker_Current() {

        Page<Booking> mockedList = new PageImpl<>(Collections.singletonList(booking));
        List<BookingDtoResponse> mockedDtoList = mockedList.stream()
                .map(BookingMapper::toBookingDtoResponse)
                .collect(Collectors.toList());
        Pageable page = PaginationUtils.createPageable(0, 10, PaginationUtils.SORT_START_DESC);

        when(bookingRepository.findByBooker_IdAndStartIsBeforeAndEndIsAfter(
                eq(bookerId), any(LocalDateTime.class), any(LocalDateTime.class), eq(page))).thenReturn(mockedList);

        List<BookingDtoResponse> result = bookingService.getBookingsForCurrentBooker(
                bookerId, BookingState.CURRENT, 0, 10);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(mockedDtoList, result);

    }

    @Test
    void testGetBookingsForCurrentBooker_Past() {

        Page<Booking> mockedList = new PageImpl<>(Collections.singletonList(booking));
        List<BookingDtoResponse> mockedDtoList = mockedList.stream()
                .map(BookingMapper::toBookingDtoResponse)
                .collect(Collectors.toList());
        Pageable page = PaginationUtils.createPageable(0, 10, PaginationUtils.SORT_START_DESC);

        when(bookingRepository.findByBooker_IdAndEndIsBefore(
                eq(bookerId), any(LocalDateTime.class), eq(page))).thenReturn(mockedList);

        List<BookingDtoResponse> result = bookingService.getBookingsForCurrentBooker(
                bookerId, BookingState.PAST, 0, 10);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(mockedDtoList, result);

    }

    @Test
    void testGetBookingsForCurrentBooker_Future() {

        Page<Booking> mockedList = new PageImpl<>(Collections.singletonList(booking));
        List<BookingDtoResponse> mockedDtoList = mockedList.stream()
                .map(BookingMapper::toBookingDtoResponse)
                .collect(Collectors.toList());
        Pageable page = PaginationUtils.createPageable(0, 10, PaginationUtils.SORT_START_DESC);

        when(bookingRepository.findByBooker_IdAndStartIsAfter(
                eq(bookerId), any(LocalDateTime.class), eq(page))).thenReturn(mockedList);

        List<BookingDtoResponse> result = bookingService.getBookingsForCurrentBooker(
                bookerId, BookingState.FUTURE, 0, 10);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(mockedDtoList, result);

    }

    @Test
    void testGetBookingsForCurrentBooker_Waiting() {

        Page<Booking> mockedList = new PageImpl<>(Collections.singletonList(booking));
        List<BookingDtoResponse> mockedDtoList = mockedList.stream()
                .map(BookingMapper::toBookingDtoResponse)
                .collect(Collectors.toList());
        Pageable page = PaginationUtils.createPageable(0, 10, PaginationUtils.SORT_START_DESC);

        when(bookingRepository.findByBookerIdAndStatus(
                bookerId, BookingStatus.WAITING, page)).thenReturn(mockedList);

        List<BookingDtoResponse> result = bookingService.getBookingsForCurrentBooker(
                bookerId, BookingState.WAITING, 0, 10);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(mockedDtoList, result);

    }

    @Test
    void testGetBookingsForCurrentBooker_Rejected() {

        Page<Booking> mockedList = new PageImpl<>(Collections.singletonList(booking));
        List<BookingDtoResponse> mockedDtoList = mockedList.stream()
                .map(BookingMapper::toBookingDtoResponse)
                .collect(Collectors.toList());
        Pageable page = PaginationUtils.createPageable(0, 10, PaginationUtils.SORT_START_DESC);

        when(bookingRepository.findByBookerIdAndStatus(
                bookerId, BookingStatus.REJECTED, page)).thenReturn(mockedList);

        List<BookingDtoResponse> result = bookingService.getBookingsForCurrentBooker(
                bookerId, BookingState.REJECTED, 0, 10);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(mockedDtoList, result);

    }

    @Test
    void testGetBookingsForCurrentBooker_BookingsNotFound_ThrowsDataNotFoundException() {

        Integer from = 0;
        Integer size = 10;
        Pageable page = PaginationUtils.createPageable(from, size, PaginationUtils.SORT_START_DESC);
        when(bookingRepository.findByBookerId(bookerId23, page)).thenReturn(new PageImpl<>(Collections.emptyList()));

        assertThrows(DataNotFoundException.class, () -> {
            bookingService.getBookingsForCurrentBooker(
                    bookerId23, BookingState.ALL, 0, 10);

        });
    }

    @Test
    void testGetBookingsForAllItemsWhereOwnerIsCurrentUser_All() {

        Page<Booking> mockedList = new PageImpl<>(Arrays.asList(booking2, booking3));
        List<BookingDtoResponse> mockedDtoList = Stream.of(booking2, booking3)
                .map(BookingMapper::toBookingDtoResponse)
                .collect(Collectors.toList());

        Integer from = 0;
        Integer size = 10;
        Pageable page = PaginationUtils.createPageable(from, size, PaginationUtils.SORT_START_DESC);
        when(bookingRepository.findByItem_Owner_Id(bookerId23, page)).thenReturn(mockedList);

        List<BookingDtoResponse> result = bookingService.getBookingsForAllItemsWhereOwnerIsCurrentUser(
                bookerId23, BookingState.ALL, 0, 10);


        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(mockedDtoList, result);

    }

    @Test
    void testGetBookingsForAllItemsWhereOwnerIsCurrentUser_Current() {

        Page<Booking> mockedList = new PageImpl<>(Collections.singletonList(booking));
        List<BookingDtoResponse> mockedDtoList = mockedList.stream()
                .map(BookingMapper::toBookingDtoResponse)
                .collect(Collectors.toList());
        Pageable page = PaginationUtils.createPageable(0, 10, PaginationUtils.SORT_START_DESC);

        when(bookingRepository.findByItem_Owner_IdAndStartIsBeforeAndEndIsAfter(
                eq(bookerId), any(LocalDateTime.class), any(LocalDateTime.class), eq(page))).thenReturn(mockedList);

        List<BookingDtoResponse> result = bookingService.getBookingsForAllItemsWhereOwnerIsCurrentUser(
                bookerId, BookingState.CURRENT, 0, 10);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(mockedDtoList, result);

    }

    @Test
    void testGetBookingsForAllItemsWhereOwnerIsCurrentUser_Past() {

        Page<Booking> mockedList = new PageImpl<>(Collections.singletonList(booking));
        List<BookingDtoResponse> mockedDtoList = mockedList.stream()
                .map(BookingMapper::toBookingDtoResponse)
                .collect(Collectors.toList());
        Pageable page = PaginationUtils.createPageable(0, 10, PaginationUtils.SORT_START_DESC);

        when(bookingRepository.findByItem_Owner_IdAndEndIsBefore(
                eq(bookerId), any(LocalDateTime.class), eq(page))).thenReturn(mockedList);

        List<BookingDtoResponse> result = bookingService.getBookingsForAllItemsWhereOwnerIsCurrentUser(
                bookerId, BookingState.PAST, 0, 10);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(mockedDtoList, result);

    }

    @Test
    void testGetBookingsForAllItemsWhereOwnerIsCurrentUser_Future() {

        Page<Booking> mockedList = new PageImpl<>(Collections.singletonList(booking));
        List<BookingDtoResponse> mockedDtoList = mockedList.stream()
                .map(BookingMapper::toBookingDtoResponse)
                .collect(Collectors.toList());
        Pageable page = PaginationUtils.createPageable(0, 10, PaginationUtils.SORT_START_DESC);

        when(bookingRepository.findByItem_Owner_IdAndStartIsAfter(
                eq(bookerId), any(LocalDateTime.class), eq(page))).thenReturn(mockedList);

        List<BookingDtoResponse> result = bookingService.getBookingsForAllItemsWhereOwnerIsCurrentUser(
                bookerId, BookingState.FUTURE, 0, 10);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(mockedDtoList, result);

    }

    @Test
    void testGetBookingsForAllItemsWhereOwnerIsCurrentUser_Waiting() {

        Page<Booking> mockedList = new PageImpl<>(Collections.singletonList(booking));
        List<BookingDtoResponse> mockedDtoList = mockedList.stream()
                .map(BookingMapper::toBookingDtoResponse)
                .collect(Collectors.toList());
        Pageable page = PaginationUtils.createPageable(0, 10, PaginationUtils.SORT_START_DESC);

        when(bookingRepository.findByItem_Owner_IdAndStatus(
                bookerId, BookingStatus.WAITING, page)).thenReturn(mockedList);

        List<BookingDtoResponse> result = bookingService.getBookingsForAllItemsWhereOwnerIsCurrentUser(
                bookerId, BookingState.WAITING, 0, 10);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(mockedDtoList, result);

    }

    @Test
    void testGetBookingsForAllItemsWhereOwnerIsCurrentUser_Rejected() {

        Page<Booking> mockedList = new PageImpl<>(Collections.singletonList(booking));
        List<BookingDtoResponse> mockedDtoList = mockedList.stream()
                .map(BookingMapper::toBookingDtoResponse)
                .collect(Collectors.toList());
        Pageable page = PaginationUtils.createPageable(0, 10, PaginationUtils.SORT_START_DESC);

        when(bookingRepository.findByItem_Owner_IdAndStatus(
                bookerId, BookingStatus.REJECTED, page)).thenReturn(mockedList);

        List<BookingDtoResponse> result = bookingService.getBookingsForAllItemsWhereOwnerIsCurrentUser(
                bookerId, BookingState.REJECTED, 0, 10);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(mockedDtoList, result);

    }

    @Test
    void testGetBookingsForAllItemsWhereOwnerIsCurrentUser_BookingsNotFound_ThrowsDataNotFoundException() {

        Integer from = 0;
        Integer size = 10;
        Pageable page = PaginationUtils.createPageable(from, size, PaginationUtils.SORT_START_DESC);
        when(bookingRepository.findByItem_Owner_Id(bookerId23, page)).thenReturn(new PageImpl<>(Collections.emptyList()));

        assertThrows(DataNotFoundException.class, () -> {
            bookingService.getBookingsForAllItemsWhereOwnerIsCurrentUser(
                    bookerId23, BookingState.ALL, 0, 10);

        });
    }


}
