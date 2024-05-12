package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDtoRequest;
import ru.practicum.shareit.user.dto.UserDtoResponse;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper mapper;

    private UserDtoRequest userDtoRequest;

    private UserDtoResponse userDtoResponse;

    @BeforeEach
    void setUp() {

        userDtoRequest = UserDtoRequest.builder()
                .name("Andrei")
                .email("mail@gmail.com")
                .build();

        userDtoResponse = UserDtoResponse.builder()
                .id(1L)
                .name("Andrei")
                .email("mail@gmail.com")
                .build();

    }

    @Test
    void testCreateUser() throws Exception {

        Mockito.when(userService.create(userDtoRequest)).thenReturn(userDtoResponse);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userDtoRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(userDtoResponse.getId()))
                .andExpect(jsonPath("$.name").value(userDtoResponse.getName()))
                .andExpect(jsonPath("$.email").value(userDtoResponse.getEmail()));

    }

    @Test
    void testUpdateUser() throws Exception {

        Mockito.when(userService.update(userDtoResponse.getId(), userDtoRequest)).thenReturn(userDtoResponse);

        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userDtoRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userDtoResponse.getId()))
                .andExpect(jsonPath("$.name").value(userDtoResponse.getName()))
                .andExpect(jsonPath("$.email").value(userDtoResponse.getEmail()));

    }

    @Test
    void testGetUserById() throws Exception {

        Mockito.when(userService.getUserDtoResponse(1L)).thenReturn(userDtoResponse);

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userDtoResponse.getId()))
                .andExpect(jsonPath("$.name").value(userDtoResponse.getName()))
                .andExpect(jsonPath("$.email").value(userDtoResponse.getEmail()));
    }

    @Test
    void testGetAllUsers() throws Exception {

        List<UserDtoResponse> users = Collections.singletonList(userDtoResponse);
        Mockito.when(userService.getAll()).thenReturn(users);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(userDtoResponse.getId()))
                .andExpect(jsonPath("$[0].name").value(userDtoResponse.getName()))
                .andExpect(jsonPath("$[0].email").value(userDtoResponse.getEmail()));
    }

    @Test
    void testDeleteUser() throws Exception {

        doNothing().when(userService).delete(1L);

        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk());

    }

    @Test
    public void whenPostRequestToUsersAndNameIsMissing_thenCorrectResponse() throws Exception {

        UserDtoRequest userDto = UserDtoRequest.builder().email("mail@gmail.com").build();
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest());

    }

    @Test
    public void whenPostRequestToUsersAndEmailIsInvalid_thenCorrectResponse() throws Exception {

        UserDtoRequest userDto = UserDtoRequest.builder().name("Andrei").email("invalid-email").build();

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest());

    }

    @Test
    public void whenPostRequestToUsersAndEmailIsNull_thenCorrectResponse() throws Exception {

        UserDtoRequest userDto = UserDtoRequest.builder().name("Andrei").build();

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest());
    }

}
