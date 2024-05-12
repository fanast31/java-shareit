package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRDtoRequest;
import ru.practicum.shareit.request.dto.ItemRDtoResponse;

import java.util.List;

public interface ItemRequestService {

    ItemRDtoResponse createRequest(long userId, ItemRDtoRequest itemDtoRequest);

    List<ItemRDtoResponse> getUserRequests(long userId);

    List<ItemRDtoResponse> getAllRequests(long userId, Integer from, Integer size);

    ItemRDtoResponse getRequest(long userId, long requestId);

}
