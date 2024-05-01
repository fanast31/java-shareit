package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exceptions.BadRequestException_400;
import ru.practicum.shareit.exceptions.DataNotFoundException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;
import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService{

    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemService itemService;

    @Override
    @Transactional
    public BookingDtoResponse createBooking(long userId, BookingDtoRequest bookingDtoRequest) {

        Booking booking = BookingMapper.toBooking(bookingDtoRequest);

        Item item = itemService.getItem(bookingDtoRequest.getItemId());
        if (!item.getAvailable()) {
            throw new BadRequestException_400("item.available = false");
        }
        User booker = userService.getUser(userId);

        booking.setItem(item);
        booking.setBooker(booker);

        return BookingMapper.toBookingDtoResponse(bookingRepository.save(booking));

    }

    @Override
    public BookingDtoResponse updateBookingStatus(long bookingId, long userId, boolean approved) {

        Booking booking = bookingRepository.bookingForUpdate(bookingId, userId)
                .orElseThrow(() -> new DataNotFoundException("Booking not found or not access for update"));

        if (approved) {
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
                list = bookingRepository.findByBookerId(userId);
            case CURRENT:
                list = bookingRepository.findByBookerId(userId).stream()
                        .filter(booking -> !now.isBefore(booking.getStart()) && !now.isAfter(booking.getEnd()))
                        .collect(Collectors.toList());
            case PAST:
                list = bookingRepository.findByBookerId(userId).stream()
                        .filter(booking -> now.isBefore(booking.getStart()))
                        .collect(Collectors.toList());
            case FUTURE:
                list = bookingRepository.findByBookerId(userId).stream()
                        .filter(booking -> now.isAfter(booking.getEnd()))
                        .collect(Collectors.toList());
            case WAITING:
                list = bookingRepository.findByBookerIdAndStatus(userId, BookingStatus.WAITING);
            case REJECTED:
                list = bookingRepository.findByBookerIdAndStatus(userId, BookingStatus.REJECTED);
        }

        if (list.isEmpty()) {
            throw new DataNotFoundException("Bookings not found");
        }

        return list.stream()
                .sorted(Comparator.comparing(Booking::getStart).reversed())
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
            case CURRENT:
                list = bookingRepository.findByItem_Owner_Id(userId).stream()
                        .filter(booking -> !now.isBefore(booking.getStart()) && !now.isAfter(booking.getEnd()))
                        .collect(Collectors.toList());
            case PAST:
                list = bookingRepository.findByItem_Owner_Id(userId).stream()
                        .filter(booking -> now.isBefore(booking.getStart()))
                        .collect(Collectors.toList());
            case FUTURE:
                list = bookingRepository.findByItem_Owner_Id(userId).stream()
                        .filter(booking -> now.isAfter(booking.getEnd()))
                        .collect(Collectors.toList());
            case WAITING:
                list = bookingRepository.findByItem_Owner_IdAndStatus(userId, BookingStatus.WAITING);
            case REJECTED:
                list = bookingRepository.findByItem_Owner_IdAndStatus(userId, BookingStatus.REJECTED);
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
