package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exceptions.DataNotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/items")
@AllArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ResponseEntity<ItemDto> create(@RequestHeader("X-Sharer-User-Id") long userId, @Valid @RequestBody ItemDto itemDto) throws ValidationException {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ItemMapper.toItemDto(itemService.create(userId, ItemMapper.toItem(itemDto)))
        );
    }

   /* @PatchMapping("/{userId}")
    public ResponseEntity<ItemDto> update(@PathVariable long itemId, @RequestBody ItemDto itemDto) throws DataNotFoundException {
        return ResponseEntity.status(HttpStatus.OK).body(
                ItemMapper.toItemDto(itemService.update(itemId, ItemMapper.toItem(itemDto)))
        );
    }
/*
    @GetMapping("/{userId}")
    public ResponseEntity<ItemDto> get(@PathVariable long itemId) {
        return ResponseEntity.status(HttpStatus.OK).body(ItemMapper.toItemDto(itemService.get(itemId)));
    }

    @GetMapping
    public ResponseEntity<List<ItemDto>> getAll() {
        return ResponseEntity.status(HttpStatus.OK).body(
                itemService.getAll().stream()
                        .map(ItemMapper::toItemDto)
                        .collect(Collectors.toList()));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> delete(@PathVariable long itemId) throws DataNotFoundException {
        itemService.delete(itemId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }*/

}
