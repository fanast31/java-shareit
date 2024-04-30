package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exceptions.DataNotFoundException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService{

    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemService itemService;

    @Override
    @Transactional
    public BookingDtoResponse createBooking(long userId, BookingDtoRequest bookingDtoRequest) {

        Booking booking = BookingMapper.toBooking(bookingDtoRequest);

        Item item = itemService.getItem(bookingDtoRequest.getItemId());
        User booker = userService.getUser(userId);

        booking.setItem(item);
        booking.setBooker(booker);

        bookingRepository.save(booking);

        return BookingMapper.toBookingDtoResponse(booking);
    }

    @Override
    public BookingDtoResponse updateBookingStatus(long bookingId, long userId, boolean approved) {

        Booking booking = bookingRepository.bookingForUpdate(bookingId, userId)
                .orElseThrow(() -> new DataNotFoundException("Booking not found or not access for update"));

        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }

        bookingRepository.save(booking);

        return BookingMapper.toBookingDtoResponse(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public BookingDtoResponse getBookingById(long userId, long bookingId) {

        Booking booking = bookingRepository.bookingForView(bookingId, userId)
                .orElseThrow(() -> new DataNotFoundException("Booking not found or not access for view"));

        return BookingMapper.toBookingDtoResponse(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDtoResponse> getBookingsForCurrentBooker(long userId, BookingState state) {
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDtoResponse> getBookingsForAllItemsWhereOwnerIsCurrentUser(long userId, BookingState state) {
        return null;
    }
}
