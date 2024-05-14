package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRDtoRequest;
import ru.practicum.shareit.utils.HttpHeaders;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> createItemRequest(
            @RequestHeader(HttpHeaders.USER_ID) long userId,
            @Valid @RequestBody ItemRDtoRequest itemDtoRequest) {
        return itemRequestClient.createItemRequest(userId, itemDtoRequest);
    }

    @GetMapping
    public ResponseEntity<Object> getUserRequests(
            @RequestHeader(HttpHeaders.USER_ID) long userId) {
        return itemRequestClient.getUserRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(
            @RequestHeader(HttpHeaders.USER_ID) long userId,
            @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
            @Positive @RequestParam(defaultValue = "10") Integer size) {
        return itemRequestClient.getAllRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequest(
            @RequestHeader(HttpHeaders.USER_ID) long userId,
            @PathVariable long requestId) {
        return itemRequestClient.getItemRequest(requestId, userId);
    }

}
