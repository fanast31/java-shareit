package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exceptions.DataNotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/items")
@AllArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ResponseEntity<ItemDto> create(
                @RequestHeader("X-Sharer-User-Id") long userId,
                @Valid @RequestBody ItemDto itemDto) throws DataNotFoundException{
        try {
            ItemDto itemDtoResponse = ItemMapper.toItemDto(itemService.create(userId, ItemMapper.toItem(itemDto)));
            return ResponseEntity.status(HttpStatus.CREATED).body(itemDtoResponse);
        } catch (DataNotFoundException dataNotFoundException) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDto> update(
                @RequestHeader("X-Sharer-User-Id") long userId,
                @PathVariable long itemId,
                @RequestBody ItemDto itemDto) throws DataNotFoundException, ValidationException {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(
                    ItemMapper.toItemDto(itemService.update(userId, itemId, ItemMapper.toItem(itemDto)))
            );
        } catch (DataNotFoundException dataNotFoundException) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (ValidationException validationException) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDto> get(@PathVariable long itemId) {
        return ResponseEntity.status(HttpStatus.OK).body(ItemMapper.toItemDto(itemService.get(itemId)));
    }

    @GetMapping
    public ResponseEntity<List<ItemDto>> getAll(@RequestHeader("X-Sharer-User-Id") long userId) {
        return ResponseEntity.status(HttpStatus.OK).body(
                itemService.getAll(userId).stream()
                        .map(ItemMapper::toItemDto)
                        .collect(Collectors.toList()));
    }

}
