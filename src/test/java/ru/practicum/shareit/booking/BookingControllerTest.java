package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.utils.HttpHeaders;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper mapper;
    @MockBean
    BookingService bookingService;

    BookingDtoRequest bookingDtoRequest;
    BookingDtoResponse bookingDtoResponse;

    @BeforeEach
    void setUp() {

        bookingDtoRequest = BookingDtoRequest.builder()
                .itemId(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        bookingDtoResponse = BookingDtoResponse.builder()
                .id(1L)
                .start(bookingDtoRequest.getStart())
                .end(bookingDtoRequest.getEnd())
                .status(BookingStatus.APPROVED)
                .build();

    }

    @Test
    public void createBooking_ShouldReturnCreated() throws Exception {

        given(bookingService.createBooking(any(Long.class), any(BookingDtoRequest.class)))
                .willReturn(bookingDtoResponse);

        mockMvc.perform(MockMvcRequestBuilders.post("/bookings")
                        .header(HttpHeaders.USER_ID, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(bookingDtoRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(bookingDtoResponse.getId()))
                .andExpect(jsonPath("$.status").value(bookingDtoResponse.getStatus().toString()));
    }

    @Test
    public void updateBookingStatus_ShouldReturnOkAndUpdatedBooking() throws Exception {

        given(bookingService.updateBookingStatus(anyLong(), anyLong(), anyBoolean()))
                .willReturn(bookingDtoResponse);

        mockMvc.perform(MockMvcRequestBuilders.patch("/bookings/1")
                        .header(HttpHeaders.USER_ID, 1L)
                        .param("approved", "true")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(bookingDtoResponse.getId()))
                .andExpect(jsonPath("$.status").value(bookingDtoResponse.getStatus().toString()));
    }

    @Test
    public void getBookingById_ShouldReturnBooking() throws Exception {

        given(bookingService.getBookingById(1L, 1L)).willReturn(bookingDtoResponse);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/bookings/1")
                        .header(HttpHeaders.USER_ID, 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        BookingDtoResponse response = mapper.readValue(responseBody, BookingDtoResponse.class);

        assertEquals(bookingDtoResponse.getId(), response.getId());
        assertEquals(bookingDtoResponse.getStatus(), response.getStatus());
        assertEquals(bookingDtoResponse.getStart(), response.getStart());
        assertEquals(bookingDtoResponse.getEnd(), response.getEnd());

    }

    @Test
    public void getBookingsForCurrentBooker_ShouldReturnBookings() throws Exception {

        List<BookingDtoResponse> bookingList = Collections.singletonList(bookingDtoResponse);

        when(bookingService.getBookingsForCurrentBooker(eq(1L), any(BookingState.class), eq(0), eq(10)))
                .thenReturn(bookingList);

        mockMvc.perform(get("/bookings")
                        .header(HttpHeaders.USER_ID, "1")
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(bookingDtoResponse.getId()))
                .andExpect(jsonPath("$[0].status").value(bookingDtoResponse.getStatus().toString()));
    }

    @Test
    public void getBookingsForCurrentBooker_WithInvalidState_ShouldThrowException() throws Exception {
        mockMvc.perform(get("/bookings")
                        .header(HttpHeaders.USER_ID, "1")
                        .param("state", "INVALID_STATE")
                        .param("from", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void getBookingsForAllItemsCurrentUser_ShouldReturnBookings() throws Exception {
        List<BookingDtoResponse> bookingList = Collections.singletonList(bookingDtoResponse);

        when(bookingService.getBookingsForAllItemsWhereOwnerIsCurrentUser(eq(1L), any(BookingState.class), eq(0), eq(10)))
                .thenReturn(bookingList);

        mockMvc.perform(get("/bookings/owner")
                        .header(HttpHeaders.USER_ID, "1")
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(bookingDtoResponse.getId()))
                .andExpect(jsonPath("$[0].status").value(bookingDtoResponse.getStatus().toString()));
    }

    @Test
    public void getBookingsForAllItemsCurrentUser_WithInvalidState_ShouldThrowException() throws Exception {
        mockMvc.perform(get("/bookings/owner")
                        .header(HttpHeaders.USER_ID, "1")
                        .param("state", "INVALID_STATE")
                        .param("from", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

}