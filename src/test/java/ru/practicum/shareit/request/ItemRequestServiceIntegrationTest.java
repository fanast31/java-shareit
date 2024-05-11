package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.request.dto.ItemRDtoRequest;
import ru.practicum.shareit.request.dto.ItemRDtoResponse;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ItemRequestServiceIntegrationTest {

    private final ItemRequestRepository requestRepository;
    private final UserRepository userRepository;
    @Autowired
    private ItemRequestService itemRequestService;

    @Test
    public void createRequestTest() {
        long userId = 1L;
        ItemRDtoRequest itemRequestDto = ItemRDtoRequest.builder()
                .description("Sample Description")
                .build();
        User user = User.builder()
                .id(1L)
                .name("user")
                .email("email@gmail.com")
                .build();
        userRepository.save(user);

        ItemRDtoResponse result = itemRequestService.createRequest(userId, itemRequestDto);

        assertEquals("Sample Description", result.getDescription());

    }

    @Test
    public void getUserRequestsTest() {

        long userId = 1L;
        ItemRDtoRequest itemRequestDto = ItemRDtoRequest.builder()
                .description("Sample Description")
                .build();
        User user = User.builder()
                .id(1L)
                .name("user")
                .email("email@gmail.com")
                .build();
        userRepository.save(user);

        List<ItemRDtoResponse> result = itemRequestService.getUserRequests(userId);

        assertEquals(0, result.size());
    }

    @Test
    public void getRequestTest() {

        long userId = 1L;
        long requestId = 1L;

        User user = User.builder()
                .id(1L)
                .name("user")
                .email("email@gmail.com")
                .build();
        userRepository.save(user);

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setDescription("Test request");
        itemRequest.setRequester(user);
        itemRequest.setCreated(LocalDateTime.now());
        ItemRequest savedRequest = requestRepository.save(itemRequest);

        ItemRDtoResponse result = itemRequestService.getRequest(userId, savedRequest.getId());

        assertEquals(requestId, result.getId());
    }

    @Test
    public void getAllRequestsTest() {

        long userId = 1L;
        int from = 0;
        int size = 10;
        User user = User.builder()
                .id(1L)
                .name("user")
                .email("email@gmail.com")
                .build();
        userRepository.save(user);

        List<ItemRDtoResponse> result = itemRequestService.getAllRequests(userId, from, size);

        assertEquals(0, result.size());
    }

}

