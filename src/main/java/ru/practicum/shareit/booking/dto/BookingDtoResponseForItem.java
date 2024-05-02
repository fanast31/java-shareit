package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.user.dto.UserDtoResponse;

import java.time.LocalDateTime;

@Data
@Builder
public class BookingDtoResponseForItem {

    private Long id;

    private LocalDateTime start;

    private LocalDateTime end;

    //private ItemDtoResponse item;

    //private UserDtoResponse booker;
    private Long bookerId;

    private BookingStatus status;

}
