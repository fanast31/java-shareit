package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ResponseEntity<ItemDto> create(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @Valid @RequestBody ItemDto itemDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ItemMapper.toItemDto(itemService.create(userId, ItemMapper.toItem(itemDto))));
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDto> update(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @PathVariable long itemId,
            @RequestBody ItemDto itemDto) {
        return ResponseEntity.ok().body(
                ItemMapper.toItemDto(itemService.update(userId, itemId, ItemMapper.toItem(itemDto)))
        );
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDto> get(@PathVariable long itemId) {
        return ResponseEntity.ok().body(ItemMapper.toItemDto(itemService.get(itemId)));
    }

    @GetMapping
    public ResponseEntity<List<ItemDto>> getAll(@RequestHeader("X-Sharer-User-Id") long userId) {
        return ResponseEntity.ok().body(
                itemService.getAll(userId).stream()
                        .map(ItemMapper::toItemDto)
                        .collect(Collectors.toList()));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDto>> searchItems(@RequestParam("text") String searchText) {
        if (searchText == null || searchText.isBlank()) {
            return ResponseEntity.ok().body(Collections.emptyList());
        }
        return ResponseEntity.ok(itemService.searchByText(searchText).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList()));
    }
}
