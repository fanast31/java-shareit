package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRDtoRequest;
import ru.practicum.shareit.request.dto.ItemRDtoResponse;
import ru.practicum.shareit.utils.HttpHeaders;
import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping
    public ResponseEntity<ItemRDtoResponse> createItemRequest(
            @RequestHeader(HttpHeaders.USER_ID) long userId,
            @RequestBody ItemRDtoRequest itemDtoRequest) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(itemRequestService.createRequest(userId, itemDtoRequest));
    }

    @GetMapping
    public ResponseEntity<List<ItemRDtoResponse>> getUserRequests(
            @RequestHeader(HttpHeaders.USER_ID) long userId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(itemRequestService.getUserRequests(userId));
    }

    @GetMapping("/all")
    public ResponseEntity<List<ItemRDtoResponse>> getAllRequests(
            @RequestHeader(HttpHeaders.USER_ID) long userId,
            @RequestParam Integer from,
            @RequestParam Integer size) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(itemRequestService.getAllRequests(userId, from, size));
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<ItemRDtoResponse> getItemRequest(
            @RequestHeader(HttpHeaders.USER_ID) long userId,
            @PathVariable long requestId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(itemRequestService.getRequest(userId, requestId));
    }

}
