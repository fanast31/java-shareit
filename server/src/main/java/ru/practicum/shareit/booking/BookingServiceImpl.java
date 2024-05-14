package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.DataNotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.utils.PaginationUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public BookingDtoResponse createBooking(long userId, BookingDtoRequest bookingDtoRequest) {

        Booking booking = BookingMapper.toBooking(bookingDtoRequest);

        Item item = itemRepository.findById(bookingDtoRequest.getItemId())
                .orElseThrow(() -> new DataNotFoundException("Item not found"));
        if (!item.getAvailable()) {
            throw new BadRequestException("item.available = false");
        }
        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("User not found"));
        if (item.getOwner().equals(booker)) {
            throw new DataNotFoundException("booker = owner");
        }

        booking.setItem(item);
        booking.setBooker(booker);

        return BookingMapper.toBookingDtoResponse(bookingRepository.save(booking));

    }

    @Override
    public BookingDtoResponse updateBookingStatus(long bookingId, long userId, boolean approved) {

        Booking booking = bookingRepository.findByIdAndItem_Owner_Id(bookingId, userId)
                .orElseThrow(() -> new DataNotFoundException("Booking not found or not access for update"));

        if (approved) {
            if (booking.getStatus() == BookingStatus.APPROVED) {
                throw new BadRequestException("Booking status is already APPROVED");
            }
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }

        return BookingMapper.toBookingDtoResponse(bookingRepository.save(booking));
    }

    @Override
    @Transactional(readOnly = true)
    public BookingDtoResponse getBookingById(long bookingId, long userId) {

        Booking booking = bookingRepository.bookingForView(bookingId, userId)
                .orElseThrow(() -> new DataNotFoundException("Booking not found or not access for view"));

        return BookingMapper.toBookingDtoResponse(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDtoResponse> getBookingsForCurrentBooker(
            long userId, BookingState state, Integer from, Integer size) {

        Pageable page = PaginationUtils.createPageable(from, size, PaginationUtils.SORT_START_DESC);

        LocalDateTime now = LocalDateTime.now();
        Page<Booking> list = null;

        switch (state) {
            case ALL:
                list = bookingRepository.findByBookerId(userId, page);
                break;
            case CURRENT:
                list = bookingRepository.findByBooker_IdAndStartIsBeforeAndEndIsAfter(
                        userId, now, now, page);
                break;
            case PAST:
                list = bookingRepository.findByBooker_IdAndEndIsBefore(userId, now, page);
                break;
            case FUTURE:
                list = bookingRepository.findByBooker_IdAndStartIsAfter(userId, now, page);
                break;
            case WAITING:
                list = bookingRepository.findByBookerIdAndStatus(userId, BookingStatus.WAITING, page);
                break;
            case REJECTED:
                list = bookingRepository.findByBookerIdAndStatus(userId, BookingStatus.REJECTED, page);
                break;
        }

        if (list.getContent().isEmpty()) {
            throw new DataNotFoundException("Bookings not found");
        }

        return list.stream()
                .map(BookingMapper::toBookingDtoResponse)
                .collect(Collectors.toList());

    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDtoResponse> getBookingsForAllItemsWhereOwnerIsCurrentUser(
            long userId, BookingState state, Integer from, Integer size) {

        Pageable page = PaginationUtils.createPageable(from, size, PaginationUtils.SORT_START_DESC);

        LocalDateTime now = LocalDateTime.now();
        Page<Booking> list = null;

        switch (state) {
            case ALL:
                list = bookingRepository.findByItem_Owner_Id(userId, page);
                break;
            case CURRENT:
                list = bookingRepository.findByItem_Owner_IdAndStartIsBeforeAndEndIsAfter(
                        userId, now, now, page);
                break;
            case PAST:
                list = bookingRepository.findByItem_Owner_IdAndEndIsBefore(userId, now, page);
                break;
            case FUTURE:
                list = bookingRepository.findByItem_Owner_IdAndStartIsAfter(userId, now, page);
                break;
            case WAITING:
                list = bookingRepository.findByItem_Owner_IdAndStatus(
                        userId, BookingStatus.WAITING, page);
                break;
            case REJECTED:
                list = bookingRepository.findByItem_Owner_IdAndStatus(
                        userId, BookingStatus.REJECTED, page);
                break;
        }

        if (list.getContent().isEmpty()) {
            throw new DataNotFoundException("Bookings not found");
        }

        return list.stream()
                .map(BookingMapper::toBookingDtoResponse)
                .collect(Collectors.toList());
    }

}
