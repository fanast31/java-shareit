package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;

import java.time.LocalDateTime;

@Data
@Builder
public class ItemDtoResponseWithBookingDatesImpl implements ItemDtoResponseWithBookingDates{

    private Long id;

    private String name;

    private String description;

    private Boolean available;

    private BookingDtoResponse lastBooking;

    private BookingDtoResponse nextBooking;

}
