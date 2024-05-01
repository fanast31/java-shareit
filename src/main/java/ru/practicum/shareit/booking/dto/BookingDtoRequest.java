package ru.practicum.shareit.booking.dto;

import lombok.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.AssertTrue;
import java.time.LocalDateTime;

@Data
public class BookingDtoRequest {

    @NotNull
    private Long itemId;

    @NotNull
    private LocalDateTime start;

    @NotNull
    private LocalDateTime end;

    @AssertTrue(message = "Start should be before end")
    public boolean isStartBeforeEnd() {
        return end != null && start != null && start.isBefore(end);
    }

}