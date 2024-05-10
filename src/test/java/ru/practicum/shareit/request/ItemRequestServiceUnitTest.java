package ru.practicum.shareit.request;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.DataNotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.ItemServiceImpl;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRDtoRequest;
import ru.practicum.shareit.request.dto.ItemRDtoResponse;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceUnitTest {

    public static final Sort SORT_CREATED_DESC = Sort.by(Sort.Direction.DESC, "created");
    public static final Sort SORT_ID_ASC = Sort.by(Sort.Direction.ASC, "id");

    @Mock
    private ItemRequestRepository requestRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private ItemRequestRepository itemRequestRepository;


    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    User requester;

    ItemRequest itemRequest;
    ItemRequest itemRequestSaved;
    ItemRDtoRequest itemRequestDto;
    ItemRDtoResponse itemRDtoResponse;


    @BeforeEach
    void setUp() {

        requester = User.builder()
                .id(1L)
                .name("requester1")
                .email("requester1@gmail.com")
                .build();

        itemRequestDto = ItemRDtoRequest.builder()
                .description("1")
                .build();

        itemRequest = ItemRequest.builder()
                .created(LocalDateTime.now())
                .description(itemRequestDto.getDescription())
                .requester(requester)
                .build();

        itemRequestSaved = ItemRequest.builder()
                .id(1L)
                .created(LocalDateTime.now())
                .description(itemRequestDto.getDescription())
                .requester(requester)
                .build();

        itemRDtoResponse = ItemRDtoResponse.builder()
                .id(1L)
                .description(itemRequestDto.getDescription())
                .items(Collections.emptyList())
                .build();

    }

    @Test
    public void createRequestTest() {

        when(userRepository.findById(requester.getId())).thenReturn(Optional.of(requester));
        when(requestRepository.save(any(ItemRequest.class))).thenReturn(itemRequestSaved);
        ItemRDtoResponse expected = ItemRequestMapper.toItemRDtoResponse(itemRequestSaved);

        ItemRDtoResponse result = itemRequestService.createRequest(requester.getId(), itemRequestDto);

        assertNotNull(result);
        Assertions.assertEquals(expected, result);
    }

    @Test
    public void getUserRequestsTest() {

        long userId = 1L;
        User mockUser = User.builder()
                .id(userId)
                .build();

        ItemRequest itemRequest1 = ItemRequest.builder()
                .id(1L)
                .requester(mockUser)
                .build();

        ItemRequest itemRequest2 = ItemRequest.builder()
                .id(2L)
                .requester(mockUser)
                .build();

        Item item1 = Item.builder()
                .id(1L)
                .request(itemRequest1)
                .build();

        Item item2 = Item.builder()
                .id(1L)
                .request(itemRequest1)
                .build();

        Item item3 = Item.builder()
                .id(1L)
                .request(itemRequest2)
                .build();
        List<Item> items = List.of(item1, item2, item3);

        Map<ItemRequest, List<Item>> mockMap = new HashMap<>();
        mockMap.put(itemRequest1, List.of(item1, item2));
        mockMap.put(itemRequest2, List.of(item3));

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(itemRepository.findAllByRequestIn(anyList(), eq(SORT_ID_ASC))).thenReturn(items);

        List<ItemRDtoResponse> responses = itemRequestService.getUserRequests(userId);

        assertNotNull(responses);

    }

    @Test
    public void getAllRequestsTest() {

        Integer from = 0, size = 10;
        long userId = 1L;
        User mockUser = User.builder()
                .id(userId)
                .build();

        ItemRequest itemRequest1 = ItemRequest.builder()
                .id(1L)
                .requester(mockUser)
                .build();

        ItemRequest itemRequest2 = ItemRequest.builder()
                .id(2L)
                .requester(mockUser)
                .build();

        Item item1 = Item.builder()
                .id(1L)
                .request(itemRequest1)
                .build();

        Item item2 = Item.builder()
                .id(1L)
                .request(itemRequest1)
                .build();

        Item item3 = Item.builder()
                .id(1L)
                .request(itemRequest2)
                .build();
        List<Item> items = List.of(item1, item2, item3);

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(itemRepository.findAllByRequestIn(anyList(), eq(SORT_ID_ASC))).thenReturn(items);

        List<ItemRDtoResponse> responses = itemRequestService.getAllRequests(userId, from, size);

        assertNotNull(responses);

    }

    @Test
    public void getRequestTest() {

        long userId = 1L;
        long requestId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        ItemRequest itemRequest = ItemRequest.builder()
                .id(1L)
                .build();
        when(requestRepository.findById(requestId)).thenReturn(Optional.of(itemRequest));

        ItemRDtoResponse result = itemRequestService.getRequest(userId, requestId);
        result.setItems(null);

        ItemRDtoResponse expectedRequest = ItemRequestMapper.toItemRDtoResponse(itemRequest);

        assertNotNull(result);
        Assertions.assertEquals(expectedRequest, result);
    }

    @Test
    public void getAllRequestsTestWithInvalidPaginationParams() {
        long userId = 1L;
        int from = -1;
        int size = 10;
        User user = new User();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            itemRequestService.getAllRequests(userId, from, size);
        });

        Assertions.assertEquals("Bad pagination data", exception.getMessage());
    }
}

