package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingSreviceImpl implements BookingService{

    private final BookingRepository bookingRepository;

    @Override
    public BookingDto create(long userId, BookingDto bookingDto) {
        return null;
    }

    @Override
    public BookingDto update(long userId, long itemId, BookingDto itemDto) {
        return null;
    }

    @Override
    public BookingDto get(long itemId) {
        return null;
    }

    @Override
    public List<BookingDto> getAll(long userId) {
        return null;
    }

    @Override
    public void delete(long itemId) {

    }
}
