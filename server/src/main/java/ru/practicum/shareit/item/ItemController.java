package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.utils.HttpHeaders;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ResponseEntity<ItemDtoResponse> createItem(
            @RequestHeader(HttpHeaders.USER_ID) long userId,
            @RequestBody ItemDtoRequest itemDtoRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(itemService.createItem(userId, itemDtoRequest));
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDtoResponse> update(
            @RequestHeader(HttpHeaders.USER_ID) long userId,
            @PathVariable long itemId,
            @RequestBody ItemDtoRequest itemDtoRequest) {
        return ResponseEntity.ok(itemService.update(userId, itemId, itemDtoRequest));
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDtoResponseWithBookingDates> get(
            @RequestHeader(HttpHeaders.USER_ID) long userId,
            @PathVariable long itemId) {
        return ResponseEntity.ok(itemService.getItemDtoResponse(itemId, userId));
    }

    @GetMapping
    public ResponseEntity<List<ItemDtoResponseWithBookingDates>> getAll(
            @RequestHeader(HttpHeaders.USER_ID) long userId,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size) {
        return ResponseEntity.ok(itemService.getAll(userId, from, size));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDtoResponse>> searchItems(
            @RequestParam("text") String searchText,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size) {
        if (searchText == null || searchText.isBlank()) {
            return ResponseEntity.ok().body(Collections.emptyList());
        }
        return ResponseEntity.ok(itemService.searchByText(searchText, from, size));
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<CommentDtoResponse> createComment(
            @RequestHeader(HttpHeaders.USER_ID) long userId,
            @PathVariable long itemId,
            @RequestBody CommentDtoRequest commentDtoRequest) {
        return ResponseEntity.ok(itemService.createComment(userId, itemId, commentDtoRequest));
    }

}
