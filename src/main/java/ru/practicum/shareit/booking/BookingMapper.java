
package ru.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.model.Booking;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookingMapper {

    public static BookingDtoResponse toBookingDtoResponse(Booking booking) {
        if (booking == null) {
            return null;
        }
        return new BookingDtoResponse(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getItem(),
                booking.getBooker(),
                booking.getStatus()
        );
    }

    public static Booking toBooking(BookingDtoRequest bookingDtoRequest) {
        if (bookingDtoRequest == null) {
            return null;
        }
        return new Booking(
                bookingDtoRequest.getStart(),
                bookingDtoRequest.getEnd()
        );
    }

}
