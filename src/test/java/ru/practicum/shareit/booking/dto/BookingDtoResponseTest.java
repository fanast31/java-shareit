package ru.practicum.shareit.booking.dto;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.user.dto.UserDtoResponse;

import java.time.LocalDateTime;

@JsonTest
class BookingDtoResponseTest {
    @Autowired
    private JacksonTester<BookingDtoResponse> json;

    @Test
    void testSerialize() throws Exception {

        LocalDateTime localDateTime = LocalDateTime.now();
        var dto = BookingDtoResponse.builder()
                .id(1L)
                .start(localDateTime.plusDays(1))
                .end(localDateTime.plusDays(2))
                .item(ItemDtoResponse.builder()
                        .id(1L)
                        .name("name")
                        .description("desc")
                        .available(true)
                        .build())
                .booker(UserDtoResponse.builder()
                        .id(1L)
                        .name("Andrei")
                        .email("mail@gmail.com")
                        .build())
                .status(BookingStatus.APPROVED)
                .build();
        var result = json.write(dto);

        Assertions.assertThat(result).hasJsonPath("$.id");
        Assertions.assertThat(result).hasJsonPath("$.start");
        Assertions.assertThat(result).hasJsonPath("$.end");
        Assertions.assertThat(result).hasJsonPath("$.item");
        Assertions.assertThat(result).hasJsonPath("$.booker");
        Assertions.assertThat(result).hasJsonPath("$.status");

    }
}