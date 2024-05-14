package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.model.BookingState;

import java.util.List;

public interface BookingService {

    BookingDtoResponse createBooking(long userId, BookingDtoRequest bookingDto);

    BookingDtoResponse updateBookingStatus(long bookingId, long userId, boolean approved);

    BookingDtoResponse getBookingById(long bookingId, long userId);

    List<BookingDtoResponse> getBookingsForCurrentBooker(
            long userId, BookingState state, Integer from, Integer size);

    List<BookingDtoResponse> getBookingsForAllItemsWhereOwnerIsCurrentUser(
            long userId, BookingState state, Integer from, Integer size);

}
