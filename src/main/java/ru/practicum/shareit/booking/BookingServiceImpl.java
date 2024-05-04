package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.DataNotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    public static final Sort SORT_START_DESC = Sort.by(Sort.Direction.DESC, "start");


    @Override
    @Transactional
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
    public List<BookingDtoResponse> getBookingsForCurrentBooker(long userId, BookingState state) {

        LocalDateTime now = LocalDateTime.now();
        List<Booking> list = new ArrayList<>();

        switch (state) {
            case ALL:
                list = bookingRepository.findByBookerId(userId, SORT_START_DESC);
                break;
            case CURRENT:
                list = bookingRepository.findByBooker_IdAndStartIsBeforeAndEndIsAfter(userId, now, now, SORT_START_DESC);
                break;
            case PAST:
                list = bookingRepository.findByBooker_IdAndEndIsBefore(userId, now, SORT_START_DESC);
                break;
            case FUTURE:
                list = bookingRepository.findByBooker_IdAndStartIsAfter(userId, now, SORT_START_DESC);
                break;
            case WAITING:
                list = bookingRepository.findByBookerIdAndStatus(userId, BookingStatus.WAITING);
                break;
            case REJECTED:
                list = bookingRepository.findByBookerIdAndStatus(userId, BookingStatus.REJECTED);
                break;
        }

        if (list.isEmpty()) {
            throw new DataNotFoundException("Bookings not found");
        }

        return list.stream()
                .map(BookingMapper::toBookingDtoResponse)
                .collect(Collectors.toList());

    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDtoResponse> getBookingsForAllItemsWhereOwnerIsCurrentUser(long userId, BookingState state) {

        LocalDateTime now = LocalDateTime.now();
        List<Booking> list = new ArrayList<>();

        switch (state) {
            case ALL:
                list = bookingRepository.findByItem_Owner_Id(userId);
                break;
            case CURRENT:
                list = bookingRepository.findByItem_Owner_Id(userId).stream()
                        .filter(booking -> !now.isBefore(booking.getStart()) && !now.isAfter(booking.getEnd()))
                        .collect(Collectors.toList());
                break;
            case PAST:
                list = bookingRepository.findByItem_Owner_Id(userId).stream()
                        .filter(booking -> now.isAfter(booking.getEnd()))
                        .collect(Collectors.toList());
                break;
            case FUTURE:
                list = bookingRepository.findByItem_Owner_Id(userId).stream()
                        .filter(booking -> now.isBefore(booking.getStart()))
                        .collect(Collectors.toList());
                break;
            case WAITING:
                list = bookingRepository.findByItem_Owner_IdAndStatus(userId, BookingStatus.WAITING);
                break;
            case REJECTED:
                list = bookingRepository.findByItem_Owner_IdAndStatus(userId, BookingStatus.REJECTED);
                break;
        }

        if (list.isEmpty()) {
            throw new DataNotFoundException("Bookings not found");
        }

        return list.stream()
                .sorted(Comparator.comparing(Booking::getStart).reversed())
                .map(BookingMapper::toBookingDtoResponse)
                .collect(Collectors.toList());
    }

}
