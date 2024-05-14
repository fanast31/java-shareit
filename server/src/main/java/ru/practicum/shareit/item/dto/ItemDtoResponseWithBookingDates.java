package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingDtoResponseForItem;

import java.util.List;

@Data
@Builder
public class ItemDtoResponseWithBookingDates {

    private Long id;

    private String name;

    private String description;

    private Boolean available;

    private BookingDtoResponseForItem lastBooking;

    private BookingDtoResponseForItem nextBooking;

    private List<CommentDtoResponse> comments;

    private Long requestId;

}
