package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingService {

    BookingDtoResponse createBooking(long userId, BookingDtoRequest bookingDto);

    BookingDtoResponse updateBookingStatus(long bookingId, long userId, boolean approved);

    BookingDtoResponse getBookingById(long bookingId, long userId);

    List<BookingDtoResponse> getBookingsForCurrentBooker(long userId, BookingState state);

    List<BookingDtoResponse> getBookingsForAllItemsWhereOwnerIsCurrentUser(long userId, BookingState state);

}
