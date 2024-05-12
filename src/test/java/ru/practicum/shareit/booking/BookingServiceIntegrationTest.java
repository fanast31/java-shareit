package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserDtoResponse;
import ru.practicum.shareit.user.model.User;

import javax.transaction.Transactional;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class BookingServiceIntegrationTest {

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingService bookingService;

    Item item;
    User owner;
    User booker;
    Booking booking;
    LocalDateTime localDateTime;
    BookingDtoResponse bookingDtoResponse;
    BookingDtoRequest bookingDtoRequest;


    @BeforeEach
    void setUp() {

        owner = User.builder()
                .name("user")
                .email("email@gmail.com")
                .build();

        booker = User.builder()
                .name("user2")
                .email("email2@gmail.com")
                .build();

        item = Item.builder()
                .name("drill")
                .description("drilling drill")
                .owner(owner)
                .available(true)
                .build();

        booking = Booking.builder()
                .item(item)
                .booker(booker)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(BookingStatus.WAITING)
                .build();

        localDateTime = LocalDateTime.now();

        bookingDtoRequest = BookingDtoRequest.builder()
                .itemId(1L)
                .start(localDateTime.plusDays(1))
                .end(localDateTime.plusDays(2))
                .build();

        bookingDtoResponse = BookingDtoResponse.builder()
                .id(2L)
                .start(localDateTime.plusDays(1))
                .end(localDateTime.plusDays(2))
                .item(ItemMapper.toItemDtoResponse(item))
                .booker(UserMapper.toUserDtoResponse(booker))
                .status(BookingStatus.APPROVED)
                .build();
    }

    @Test
    void createBooking() {

        userRepository.save(owner);
        UserDtoResponse bookerDto = UserMapper.toUserDtoResponse(userRepository.save(booker));
        ItemDtoResponse itemDto = ItemMapper.toItemDtoResponse(itemRepository.save(item));

        BookingDtoResponse createdBooking = bookingService.createBooking(bookerDto.getId(), bookingDtoRequest);

        assertNotNull(createdBooking.getId());
        assertEquals(itemDto.getId(), createdBooking.getItem().getId());
        assertEquals(bookerDto.getId(), createdBooking.getBooker().getId());

    }

    @Test
    void updateBookingStatus() {

        User ownerSaved = userRepository.save(owner);
        User bookerSaved = userRepository.save(booker);
        itemRepository.save(item);
        BookingDtoResponse createdBooking = bookingService.createBooking(bookerSaved.getId(), bookingDtoRequest);

        BookingDtoResponse createdBooking2 = bookingService.updateBookingStatus(
                createdBooking.getId(),
                ownerSaved.getId(),
                true);

        Assertions.assertEquals(BookingStatus.APPROVED, createdBooking2.getStatus());

    }

}
