package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.booking.dto.BookingDtoResponse;

public interface ItemDtoResponseWithBookingDates {

    Long getId();

    String getName();

    String getDescription();

    Boolean getAvailable();

    BookingDtoResponse getLastBooking();

    BookingDtoResponse getNextBooking();

}
