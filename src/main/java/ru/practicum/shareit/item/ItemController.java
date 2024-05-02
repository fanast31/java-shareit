package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDtoRequest;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.dto.ItemDtoResponseWithBookingDates;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ResponseEntity<ItemDtoResponse> create(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @Valid @RequestBody ItemDtoRequest itemDtoRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(itemService.create(userId, itemDtoRequest));
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDtoResponse> update(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @PathVariable long itemId,
            @RequestBody ItemDtoRequest itemDtoRequest) {
        return ResponseEntity.ok().body(itemService.update(userId, itemId, itemDtoRequest));
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDtoResponseWithBookingDates> get(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @PathVariable long itemId) {
        return ResponseEntity.ok().body(itemService.getItemDtoResponse(itemId, userId));
    }

    @GetMapping
    public ResponseEntity<List<ItemDtoResponseWithBookingDates>> getAll(@RequestHeader("X-Sharer-User-Id") long userId) {
        return ResponseEntity.ok().body(itemService.getAll(userId));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDtoResponse>> searchItems(@RequestParam("text") String searchText) {
        if (searchText == null || searchText.isBlank()) {
            return ResponseEntity.ok().body(Collections.emptyList());
        }
        return ResponseEntity.ok(itemService.searchByText(searchText));
    }
}
