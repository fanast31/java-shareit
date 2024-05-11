package ru.practicum.shareit.request.dto;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

@JsonTest
class ItemRDtoRequestTest {
    @Autowired
    private JacksonTester<ItemRDtoRequest> json;

    @Test
    void testSerialize() throws Exception {

        var dto = ItemRDtoRequest.builder()
                .id(1L)
                .description("desc")
                .build();

        var result = json.write(dto);
        Assertions.assertThat(result).hasJsonPath("$.id");
        Assertions.assertThat(result).hasJsonPath("$.description");

    }
}