package ru.practicum.shareit.booking.dto;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.time.LocalDateTime;

@JsonTest
class BookingDtoRequestTest {
    @Autowired
    private JacksonTester<BookingDtoRequest> json;

    @Test
    void testSerialize() throws Exception {

        LocalDateTime localDateTime = LocalDateTime.now();
        var dto = BookingDtoRequest.builder()
                .itemId(1L)
                .start(localDateTime.plusDays(1))
                .end(localDateTime.plusDays(2))
                .build();

        var result = json.write(dto);
        Assertions.assertThat(result).hasJsonPath("$.itemId");
        Assertions.assertThat(result).hasJsonPath("$.start");
        Assertions.assertThat(result).hasJsonPath("$.end");

    }
}