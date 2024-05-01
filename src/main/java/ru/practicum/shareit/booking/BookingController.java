package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.exceptions.UnsupportedStatusException;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingDtoResponse> createBooking(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @Valid @RequestBody BookingDtoRequest bookingDtoRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bookingService.createBooking(userId, bookingDtoRequest));
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<BookingDtoResponse> updateBookingStatus(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @PathVariable long bookingId,
            @RequestParam boolean approved) {
        return ResponseEntity.ok().body(bookingService.updateBookingStatus(bookingId, userId, approved));
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingDtoResponse> getBookingById(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @PathVariable long bookingId) {
        return ResponseEntity.ok().body(bookingService.getBookingById(bookingId, userId));
    }

    @GetMapping
    public ResponseEntity<List<BookingDtoResponse>> getBookingsForCurrentBooker(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @Valid @RequestParam(required = false, defaultValue = "ALL") String state) {
        BookingState bookingState;
        try {
            bookingState = BookingState.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new UnsupportedStatusException(e.getMessage());
        }
        return ResponseEntity.ok().body(bookingService.getBookingsForCurrentBooker(userId, bookingState));
    }

    @GetMapping("/owner")
    public ResponseEntity<List<BookingDtoResponse>> getBookingsForAllItemsCurrentUser(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @Valid @RequestParam(required = false, defaultValue = "ALL") String state) {
        BookingState bookingState;
        try {
            bookingState = BookingState.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new UnsupportedStatusException(e.getMessage());
        }
        return ResponseEntity.ok().body(bookingService.getBookingsForAllItemsWhereOwnerIsCurrentUser(userId, bookingState));
    }

}

