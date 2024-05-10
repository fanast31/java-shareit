package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exceptions.DataNotFoundException;
import ru.practicum.shareit.request.dto.ItemRDtoRequest;
import ru.practicum.shareit.request.dto.ItemRDtoResponse;
import ru.practicum.shareit.utils.HttpHeaders;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    ObjectMapper mapper;

    @MockBean
    private ItemRequestService itemRequestService;

    @Test
    public void createItemRequest_Success() throws Exception {
        ItemRDtoRequest itemRequestDto = ItemRDtoRequest.builder()
                .id(1L)
                .description("Test")
                .build();

        ItemRDtoResponse itemResponseDto = ItemRDtoResponse.builder()
                .id(1L)
                .description("Test")
                .created(LocalDateTime.now())
                .items(Collections.emptyList())
                .build();

        given(itemRequestService.createRequest(anyLong(), any(ItemRDtoRequest.class))).willReturn(itemResponseDto);

        mockMvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.USER_ID, 111)
                        .content(mapper.writeValueAsString(itemRequestDto)))
                .andExpect(status().isCreated());
    }

    @Test
    public void getUserRequests_NoDataFound() throws Exception {
        long userId = 2L;

        given(itemRequestService.getUserRequests(userId)).willReturn(Collections.emptyList());

        mockMvc.perform(get("/requests")
                        .header(HttpHeaders.USER_ID, userId))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    public void getUserRequests_ServiceThrowsException() throws Exception {
        long userId = 3L;

        given(itemRequestService.getUserRequests(userId)).willThrow(new RuntimeException("Unexpected error"));

        mockMvc.perform(get("/requests")
                        .header(HttpHeaders.USER_ID, userId))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void createItemRequest_InvalidData() throws Exception {

        long userId = 1L;

        mockMvc.perform(post("/item")
                        .header(HttpHeaders.USER_ID, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isNotFound());

    }

    @Test
    public void getUserRequests_Success() throws Exception {

        long userId = 1L;
        List<ItemRDtoResponse> responseList = Arrays.asList(
                ItemRDtoResponse.builder()
                        .id(1L)
                        .description("Test")
                        .created(LocalDateTime.now())
                        .items(Collections.emptyList())
                        .build(),
                ItemRDtoResponse.builder()
                        .id(2L)
                        .description("Test 2")
                        .created(LocalDateTime.now())
                        .items(Collections.emptyList())
                        .build()
        );

        given(itemRequestService.getUserRequests(userId)).willReturn(responseList);

        mockMvc.perform(get("/requests")
                        .header(HttpHeaders.USER_ID, userId))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(responseList)));
    }

    @Test
    public void getAllRequests_Success() throws Exception {
        long userId = 1L;
        int from = 0;
        int size = 10;

        List<ItemRDtoResponse> responseList = Arrays.asList(
                ItemRDtoResponse.builder()
                        .id(1L)
                        .description("Test Description 1")
                        .created(LocalDateTime.now())
                        .items(Collections.emptyList())
                        .build(),
                ItemRDtoResponse.builder()
                        .id(2L)
                        .description("Test Description 2")
                        .created(LocalDateTime.now())
                        .items(Collections.emptyList())
                        .build()
        );

        given(itemRequestService.getAllRequests(userId, from, size)).willReturn(responseList);

        mockMvc.perform(get("/requests/all")
                        .header(HttpHeaders.USER_ID, userId)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(responseList)));
    }

    @Test
    public void getAllRequests_NoDataFound() throws Exception {
        long userId = 2L;
        int from = 0;
        int size = 10;

        given(itemRequestService.getAllRequests(userId, from, size)).willReturn(Collections.emptyList());

        mockMvc.perform(get("/requests/all")
                        .header(HttpHeaders.USER_ID, userId)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    public void getItemRequest_Success() throws Exception {
        long userId = 1L;
        long requestId = 100L;

        ItemRDtoResponse itemResponseDto = ItemRDtoResponse.builder()
                .id(requestId)
                .description("Test Description")
                .created(LocalDateTime.now())
                .items(Collections.emptyList())
                .build();

        given(itemRequestService.getRequest(userId, requestId)).willReturn(itemResponseDto);

        mockMvc.perform(get("/requests/" + requestId)
                        .header(HttpHeaders.USER_ID, userId))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(itemResponseDto)));
    }

    @Test
    public void getItemRequest_NotFound() throws Exception {
        long userId = 1L;
        long requestId = 101L;

        given(itemRequestService.getRequest(userId, requestId)).willThrow(new DataNotFoundException("Request not found"));

        mockMvc.perform(get("/requests/" + requestId)
                        .header(HttpHeaders.USER_ID, userId))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getItemRequest_InternalServerError() throws Exception {
        long userId = 1L;
        long requestId = 102L;

        given(itemRequestService.getRequest(userId, requestId)).willThrow(new RuntimeException("Internal server error"));

        mockMvc.perform(get("/requests/" + requestId)
                        .header(HttpHeaders.USER_ID, userId))
                .andExpect(status().isInternalServerError());
    }

}