package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.ItemDtoRequest;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.dto.ItemDtoResponseWithBookingDates;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ItemMapperTest {

    @Test
    public void shouldReturnNullIfItemIsNull1() {
        assertNull(ItemMapper.toItemDtoResponse(null));
    }

    @Test
    void toItemDtoResponse() {

        ItemRequest request = new ItemRequest();
        request.setId(10L);
        Item item = Item.builder()
                .id(1L)
                .name("Drill")
                .description("Drill")
                .available(true)
                .request(request)
                .build();

        ItemDtoResponse dto = ItemMapper.toItemDtoResponse(item);

        assertEquals(item.getId(), dto.getId());
        assertEquals(item.getName(), dto.getName());
        assertEquals(item.getDescription(), dto.getDescription());
        assertEquals(item.getAvailable(), dto.getAvailable());
        assertEquals(item.getRequest().getId(), dto.getRequestId());

    }

    @Test
    public void shouldReturnNullIfItemIsNull2() {
        assertNull(ItemMapper.toItemDtoResponseWithBookingDates(null));
    }

    @Test
    void toItemDtoResponseWithBookingDates() {

        ItemRequest request = new ItemRequest();
        request.setId(10L);
        Item item = Item.builder()
                .id(1L)
                .name("Drill")
                .description("Drill")
                .available(true)
                .request(request)
                .build();

        ItemDtoResponseWithBookingDates dto = ItemMapper.toItemDtoResponseWithBookingDates(item);

        assertEquals(item.getId(), dto.getId());
        assertEquals(item.getName(), dto.getName());
        assertEquals(item.getDescription(), dto.getDescription());
        assertEquals(item.getAvailable(), dto.getAvailable());
        assertEquals(item.getRequest().getId(), dto.getRequestId());

    }

    @Test
    public void shouldReturnNullIfItemDtoRequestIsNull() {
        assertNull(ItemMapper.toItem(null));
    }

    @Test
    void toItem() {

        ItemDtoRequest dtoRequest = ItemDtoRequest.builder()
                .name("Drill")
                .description("Drill1")
                .available(true)
                .build();

        Item item = ItemMapper.toItem(dtoRequest);

        assertNull(item.getId());
        assertEquals("Drill", item.getName());
        assertEquals("Drill1", item.getDescription());
        assertEquals(true, item.getAvailable());

    }

}