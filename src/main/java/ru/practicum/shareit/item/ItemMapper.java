package ru.practicum.shareit.item;

import lombok.*;
import ru.practicum.shareit.item.dto.ItemDtoRequest;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.dto.ItemDtoResponseWithBookingDates;
import ru.practicum.shareit.item.model.Item;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemMapper {
    public static ItemDtoResponse toItemDtoResponse(Item item) {
        if (item == null) {
            return null;
        }
        return ItemDtoResponse.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
    }

    public static ItemDtoResponseWithBookingDates toItemDtoResponseWithBookingDates(ItemDtoResponseWithBookingDates item) {
        if (item == null) {
            return null;
        }
        return ItemDtoResponseWithBookingDates.builder()
                .id(item.getId())
                .name(item.getName())
                .nextBooking(item.getNextBooking())
                .lastBooking(item.getLastBooking())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
    }

    public static Item toItem(ItemDtoRequest itemDtoRequest) {
        if (itemDtoRequest == null) {
            return null;
        }
        return Item.builder()
                .name(itemDtoRequest.getName())
                .description(itemDtoRequest.getDescription())
                .available(itemDtoRequest.getAvailable())
                .build();
    }
}
