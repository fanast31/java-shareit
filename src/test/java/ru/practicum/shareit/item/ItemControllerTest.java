package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.utils.HttpHeaders;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {

    @Autowired
    ObjectMapper mapper;
    ItemDtoRequest itemDtoRequest;
    ItemDtoResponse itemDtoResponse;
    ItemDtoResponseWithBookingDates itemDtoResponseWithBookingDates;
    long userId;
    long itemId;
    @Autowired
    private MockMvc mvc;
    @MockBean
    private ItemService itemService;

    @BeforeEach
    void setUp() {

        userId = 123L;
        itemId = 1L;

        itemDtoRequest = ItemDtoRequest.builder()
                .name("drill")
                .description("drill for drilling")
                .available(true)
                .build();

        itemDtoResponse = ItemDtoResponse.builder()
                .id(itemId)
                .name(itemDtoRequest.getName())
                .description(itemDtoRequest.getDescription())
                .available(itemDtoRequest.getAvailable())
                .build();

        itemDtoResponseWithBookingDates = ItemDtoResponseWithBookingDates.builder()
                .id(itemId)
                .name(itemDtoRequest.getName())
                .description(itemDtoRequest.getDescription())
                .available(itemDtoRequest.getAvailable())
                .build();

    }

    @Test
    void createItem() throws Exception {

        when(itemService.createItem(itemId, itemDtoRequest)).thenReturn(itemDtoResponse);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDtoRequest))
                        .header(HttpHeaders.USER_ID, itemId)
                        .contentType("application/json"))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verify(itemService).createItem(itemId, itemDtoRequest);

    }

    @Test
    void update() throws Exception {

        when(itemService.update(userId, itemId, itemDtoRequest))
                .thenReturn(itemDtoResponse);

        mvc.perform(patch("/items/{itemId}", itemId)
                        .header(HttpHeaders.USER_ID, String.valueOf(userId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(itemDtoRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDtoResponse.getId()))
                .andExpect(jsonPath("$.name").value(itemDtoResponse.getName()))
                .andExpect(jsonPath("$.description").value(itemDtoResponse.getDescription()))
                .andExpect(jsonPath("$.available").value(itemDtoResponse.getAvailable()));

    }

    @Test
    void getItemDtoResponse() throws Exception {

        Mockito
                .when(itemService.getItemDtoResponse(itemId, userId))
                .thenReturn(itemDtoResponseWithBookingDates);

        mvc.perform(get("/items/{itemId}", itemDtoResponseWithBookingDates.getId())
                        .header(HttpHeaders.USER_ID, String.valueOf(userId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDtoResponseWithBookingDates.getId()))
                .andExpect(jsonPath("$.name").value(itemDtoResponseWithBookingDates.getName()));
    }

    @Test
    void getAll_ShouldReturnItems() throws Exception {
        List<ItemDtoResponseWithBookingDates> items = new ArrayList<>();
        items.add(itemDtoResponseWithBookingDates);
        items.add(ItemDtoResponseWithBookingDates.builder()
                .id(2L)
                .name("hammer")
                .description("hammer for construction")
                .available(true)
                .build());

        when(itemService.getAll(userId, 0, 10)).thenReturn(items);

        mvc.perform(get("/items")
                        .header(HttpHeaders.USER_ID, userId)
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(items.size()))
                .andExpect(jsonPath("$[0].id").value(items.get(0).getId()))
                .andExpect(jsonPath("$[0].name").value(items.get(0).getName()))
                .andExpect(jsonPath("$[1].id").value(items.get(1).getId()))
                .andExpect(jsonPath("$[1].name").value(items.get(1).getName()));
        verify(itemService).getAll(userId, 0, 10);
    }

    @Test
    void getAll_ShouldReturnEmptyList() throws Exception {
        when(itemService.getAll(userId, 0, 10)).thenReturn(Collections.emptyList());

        mvc.perform(get("/items")
                        .header(HttpHeaders.USER_ID, userId)
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(0));
        verify(itemService).getAll(userId, 0, 10);
    }

    @Test
    void searchItems_ShouldReturnFoundItems() throws Exception {

        List<ItemDtoResponse> foundItems = List.of(
                ItemDtoResponse.builder()
                        .id(1L)
                        .name("drill")
                        .description("Powerful cordless drill")
                        .available(true)
                        .build(),
                ItemDtoResponse.builder()
                        .id(2L)
                        .name("screwdriver")
                        .description("Electric screwdriver")
                        .available(true)
                        .build()
        );

        when(itemService.searchByText("drill", 0, 10)).thenReturn(foundItems);

        mvc.perform(get("/items/search")
                        .param("text", "drill")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("drill"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].name").value("screwdriver"));
        verify(itemService).searchByText("drill", 0, 10);
    }

    @Test
    void createComment_ShouldReturnCreatedComment() throws Exception {

        CommentDtoRequest commentDtoRequest = CommentDtoRequest.builder()
                .text("Great tool!")
                .build();

        CommentDtoResponse commentDtoResponse = CommentDtoResponse.builder()
                .id(1L)
                .text("Great tool!")
                .authorName("John Doe")
                .created(LocalDateTime.now())
                .build();

        when(itemService.createComment(userId, itemId, commentDtoRequest))
                .thenReturn(commentDtoResponse);

        mvc.perform(post("/items/{itemId}/comment", itemId)
                        .header(HttpHeaders.USER_ID, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(commentDtoRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(commentDtoResponse.getId()))
                .andExpect(jsonPath("$.text").value(commentDtoResponse.getText()))
                .andExpect(jsonPath("$.authorName").value(commentDtoResponse.getAuthorName()));
        verify(itemService).createComment(userId, itemId, commentDtoRequest);
    }

}