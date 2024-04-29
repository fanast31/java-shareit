package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {

    BookingDto create(long userId, BookingDto bookingDto);

    BookingDto update(long userId, long itemId, BookingDto itemDto);

    BookingDto get(long itemId);

    List<BookingDto> getAll(long userId);

    void delete(long itemId);

}
