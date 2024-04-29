package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingState;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingDto> create(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @Valid @RequestBody BookingDto bookingDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bookingService.create(userId, bookingDto));
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<BookingDto> setStatus(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @PathVariable long bookingId,
            @RequestParam boolean approved) {
        return null;//ResponseEntity.ok().body(bookingService.setBookingStatus(userId, bookingId, approved));
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingDto> getBookingById(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam long bookingId) {
        return null;//ResponseEntity.ok().body(bookingService.get(userId, bookingId));
    }

    @GetMapping
    public ResponseEntity<BookingDto> getAllBookingsForCurrentUser(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(required = false, defaultValue = "ALL") BookingState state) {
        return null;//ResponseEntity.ok().body(bookingService.get(userId, state));
    }

    @GetMapping("/owner")
    public ResponseEntity<BookingDto> getBookingsForUserItems(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(required = false, defaultValue = "ALL") BookingState state) {
        return null;//ResponseEntity.ok().body(bookingService.get(userId, state));
    }

}

