package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.DataNotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRDtoRequest;
import ru.practicum.shareit.request.dto.ItemRDtoResponse;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.utils.PaginationUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;

    private final UserRepository userRepository;

    private final ItemRepository itemRepository;

    public static final Sort SORT_CREATED_DESC = Sort.by(Sort.Direction.DESC, "created");

    public static final Sort SORT_ID_ASC = Sort.by(Sort.Direction.ASC, "id");

    @Override
    @Transactional
    public ItemRDtoResponse createRequest(long userId, ItemRDtoRequest itemDtoRequest) {

        User user = getUserById(userId);
        ItemRequest request = ItemRequestMapper.toItemRequest(itemDtoRequest);
        request.setCreated(LocalDateTime.now());
        request.setRequester(user);

        return ItemRequestMapper.toItemRDtoResponse(itemRequestRepository.save(request));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRDtoResponse> getUserRequests(long userId) {

        User user = getUserById(userId);

        List<ItemRequest> results = itemRequestRepository.findAllByRequester(user, SORT_CREATED_DESC);
        Map<ItemRequest, List<Item>> map = getItemsMap(results);

        return mapToItemRDtoResponse(map, results);

    }

    @Override
    public List<ItemRDtoResponse> getAllRequests(long userId, Integer from, Integer size) {

        User user = getUserById(userId);

        Pageable page = PaginationUtils.createPageable(from, size, SORT_CREATED_DESC);

        List<ItemRequest> requests = itemRequestRepository.findAllByRequesterNot(user, page);
        Map<ItemRequest, List<Item>> map = getItemsMap(requests);

        return mapToItemRDtoResponse(map, requests);

    }

    @Override
    public ItemRDtoResponse getRequest(long userId, long requestId) {

        getUserById(userId);

        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new DataNotFoundException("Item request not found"));

        List<ItemDtoResponse> items = itemRepository.findAllByRequestIn(List.of(itemRequest), SORT_ID_ASC).stream()
                .map(ItemMapper::toItemDtoResponse)
                .collect(Collectors.toList());

        ItemRDtoResponse itemResponseDto = ItemRequestMapper.toItemRDtoResponse(itemRequest);
        itemResponseDto.setItems(items);

        return itemResponseDto;

    }

    private List<ItemRDtoResponse> mapToItemRDtoResponse(Map<ItemRequest, List<Item>> itemsMap, List<ItemRequest> requests) {

        return requests.stream().map(request -> {

            List<ItemDtoResponse> items = itemsMap.getOrDefault(request, Collections.emptyList())
                    .stream()
                    .map(ItemMapper::toItemDtoResponse)
                    .collect(Collectors.toList());

            ItemRDtoResponse itemRDtoResponse = ItemRequestMapper.toItemRDtoResponse(request);
            itemRDtoResponse.setItems(items);

            return itemRDtoResponse;

        }).collect(Collectors.toList());
    }

    private Map<ItemRequest, List<Item>> getItemsMap(List<ItemRequest> requests) {
        List<Item> items = itemRepository.findAllByRequestIn(requests, SORT_ID_ASC);
        return items.stream().collect(Collectors.groupingBy(Item::getRequest));
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("User not found"));
    }

}
