package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;

@Data
@Builder
public class ItemDtoResponseWithBookingDates {

    private Long id;

    private String name;

    private String description;

    private Boolean available;

    private BookingDtoResponse lastBooking;

    private BookingDtoResponse nextBooking;

}
