package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.AssertTrue;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class BookingDtoRequest {

    @NotNull
    private Long itemId;

    @NotNull
    private LocalDateTime start;

    @NotNull
    private LocalDateTime end;

    @AssertTrue(message = "Start should be after end")
    public boolean isStartBeforeEnd() {
        return start.isBefore(end);
    }

}
