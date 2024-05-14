package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.AssertTrue;
import java.time.LocalDateTime;

@Data
@Builder
public class BookingDtoRequest {

    private Long itemId;

    private LocalDateTime start;

    private LocalDateTime end;

    @AssertTrue(message = "Start should be before end")
    public boolean isStartBeforeEnd() {
        return end != null && start != null && start.isBefore(end);
    }

    @AssertTrue(message = "Now should be before start")
    public boolean isNowBeforeStart() {
        return start != null && LocalDateTime.now().isBefore(start);
    }

}
