package ru.practicum.shareit.request.dto;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.time.LocalDateTime;
import java.util.Collections;

@JsonTest
class ItemRDtoResponseTest {
    @Autowired
    private JacksonTester<ItemRDtoResponse> json;

    @Test
    void testSerialize() throws Exception {

        var dto = ItemRDtoResponse.builder()
                .id(1L)
                .description("desc")
                .items(Collections.emptyList())
                .created(LocalDateTime.now())
                .build();

        var result = json.write(dto);
        Assertions.assertThat(result).hasJsonPath("$.id");
        Assertions.assertThat(result).hasJsonPath("$.description");
        Assertions.assertThat(result).hasJsonPath("$.items");
        Assertions.assertThat(result).hasJsonPath("$.created");
    }
}