package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDtoRequest;
import ru.practicum.shareit.item.dto.ItemDtoRequest;
import ru.practicum.shareit.utils.HttpHeaders;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collections;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> createItem(
            @RequestHeader(HttpHeaders.USER_ID) long userId,
            @Valid @RequestBody ItemDtoRequest itemDtoRequest) {
        return itemClient.createItem(userId, itemDtoRequest);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(
            @RequestHeader(HttpHeaders.USER_ID) long userId,
            @PathVariable long itemId,
            @RequestBody ItemDtoRequest itemDtoRequest) {
        return itemClient.update(userId, itemId, itemDtoRequest);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> get(
            @RequestHeader(HttpHeaders.USER_ID) long userId,
            @PathVariable long itemId) {
        return itemClient.get(itemId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAll(
            @RequestHeader(HttpHeaders.USER_ID) long userId,
            @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
            @Positive @RequestParam(defaultValue = "10") Integer size) {
        return itemClient.getAll(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(
            @RequestParam("text") String searchText,
            @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
            @Positive @RequestParam(defaultValue = "10") Integer size) {
        if (searchText == null || searchText.isBlank()) {
            return ResponseEntity.ok().body(Collections.emptyList());
        }
        return itemClient.searchItems(searchText, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(
            @RequestHeader(HttpHeaders.USER_ID) long userId,
            @PathVariable long itemId,
            @Valid @RequestBody CommentDtoRequest commentDtoRequest) {
        return itemClient.createComment(userId, itemId, commentDtoRequest);
    }

}
