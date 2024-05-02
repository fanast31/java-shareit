
package ru.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.dto.BookingDtoResponseForItem;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.user.UserMapper;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookingMapper {

    public static BookingDtoResponse toBookingDtoResponse(Booking booking) {
        if (booking == null) {
            return null;
        }
        return BookingDtoResponse.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(ItemMapper.toItemDtoResponse(booking.getItem()))
                .booker(UserMapper.toUserDtoResponse(booking.getBooker()))
                .status(booking.getStatus())
                .build();

    }

    public static BookingDtoResponseForItem toBookingDtoResponseForItem(Booking booking) {
        if (booking == null) {
            return null;
        }
        return BookingDtoResponseForItem.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .bookerId(booking.getBooker().getId())
                .status(booking.getStatus())
                .build();

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
