package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.exceptions.UnsupportedStatusException;
import ru.practicum.shareit.utils.HttpHeaders;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> createBooking(
            @RequestHeader(HttpHeaders.USER_ID) long userId,
            @Valid @RequestBody BookingDtoRequest bookingDtoRequest) {
        return bookingClient.createBooking(userId, bookingDtoRequest);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> updateBookingStatus(
            @RequestHeader(HttpHeaders.USER_ID) long userId,
            @PathVariable long bookingId,
            @RequestParam boolean approved) {
        return bookingClient.updateBookingStatus(bookingId, userId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingById(
            @RequestHeader(HttpHeaders.USER_ID) long userId,
            @PathVariable long bookingId) {
        return bookingClient.getBookingById(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getBookingsForCurrentBooker(
            @RequestHeader(HttpHeaders.USER_ID) long userId,
            @Valid @RequestParam(required = false, defaultValue = "ALL") String state,
            @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
            @Positive @RequestParam(defaultValue = "10") Integer size) {
        BookingState bookingState;
        try {
            bookingState = BookingState.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new UnsupportedStatusException(e.getMessage());
        }
        return bookingClient.getBookingsForCurrentBooker(userId, bookingState, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingsForAllItemsCurrentUser(
            @RequestHeader(HttpHeaders.USER_ID) long userId,
            @Valid @RequestParam(required = false, defaultValue = "ALL") String state,
            @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
            @Positive @RequestParam(defaultValue = "10") Integer size) {
        BookingState bookingState;
        try {
            bookingState = BookingState.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new UnsupportedStatusException(e.getMessage());
        }
        return bookingClient.getBookingsForAllItemsWhereOwnerIsCurrentUser(userId, bookingState, from, size);
    }

}

