package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.utils.HttpHeaders;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingDtoResponse> createBooking(
            @RequestHeader(HttpHeaders.USER_ID) long userId,
            @RequestBody BookingDtoRequest bookingDtoRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bookingService.createBooking(userId, bookingDtoRequest));
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<BookingDtoResponse> updateBookingStatus(
            @RequestHeader(HttpHeaders.USER_ID) long userId,
            @PathVariable long bookingId,
            @RequestParam boolean approved) {
        return ResponseEntity.ok().body(bookingService.updateBookingStatus(bookingId, userId, approved));
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingDtoResponse> getBookingById(
            @RequestHeader(HttpHeaders.USER_ID) long userId,
            @PathVariable long bookingId) {
        return ResponseEntity.ok().body(bookingService.getBookingById(bookingId, userId));
    }

    @GetMapping
    public ResponseEntity<List<BookingDtoResponse>> getBookingsForCurrentBooker(
            @RequestHeader(HttpHeaders.USER_ID) long userId,
            @RequestParam BookingState state,
            @RequestParam Integer from,
            @RequestParam Integer size) {
        return ResponseEntity.ok().body(bookingService.getBookingsForCurrentBooker(userId, state, from, size));
    }

    @GetMapping("/owner")
    public ResponseEntity<List<BookingDtoResponse>> getBookingsForAllItemsCurrentUser(
            @RequestHeader(HttpHeaders.USER_ID) long userId,
            @RequestParam BookingState state,
            @RequestParam Integer from,
            @RequestParam Integer size) {
        return ResponseEntity.ok().body(
                bookingService.getBookingsForAllItemsWhereOwnerIsCurrentUser(userId, state, from, size));
    }

}

