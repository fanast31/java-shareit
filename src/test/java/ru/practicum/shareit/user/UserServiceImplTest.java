package ru.practicum.shareit.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.user.dto.UserDtoRequest;
import ru.practicum.shareit.user.dto.UserDtoResponse;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @InjectMocks
    UserServiceImpl userService;

    @Mock
    UserRepository userRepository;

    UserDtoRequest userDtoRequest1;

    User user1;

    User user2;

    UserDtoResponse userDtoResponse1;

    @BeforeEach
    void setUp() {

        userDtoRequest1 = UserDtoRequest.builder()
                .name("user1")
                .email("email1@gmail.com")
                .build();

        user1 = User.builder()
                .id(1L)
                .name("user1")
                .email("email1@gmail.com")
                .build();

        user2 = User.builder()
                .id(user1.getId())
                .name(user1.getName())
                .email(user1.getEmail())
                .build();

        userDtoResponse1 = UserDtoResponse.builder()
                .id(1L)
                .name("user1")
                .email("email1@gmail.com")
                .build();

    }

    @Test
    void create() {

        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(user1);

        UserDtoResponse actualDto = userService.create(userDtoRequest1);

        Assertions.assertNotNull(actualDto);
        Assertions.assertEquals(userDtoResponse1.getId(), actualDto.getId());
        Assertions.assertEquals(userDtoResponse1.getName(), actualDto.getName());
        Assertions.assertEquals(userDtoResponse1.getEmail(), actualDto.getEmail());

    }

    @Test
    void update() {

        Mockito.when(userRepository.findById(user1.getId())).thenReturn(Optional.of(user1));
        Mockito.when(userRepository.save(ArgumentMatchers.any(User.class))).then(AdditionalAnswers.returnsFirstArg());

        UserDtoResponse expected = userService.create(userDtoRequest1);
        expected.setId(user1.getId());
        userDtoRequest1.setName("newName");
        userDtoRequest1.setEmail("newmail@gmail.com");

        UserDtoResponse actual = userService.update(user1.getId(), userDtoRequest1);

        Assertions.assertNotNull(expected);
        Assertions.assertEquals(expected.getId(), actual.getId());
        Assertions.assertEquals(userDtoRequest1.getName(), actual.getName());
        Assertions.assertEquals(userDtoRequest1.getEmail(), actual.getEmail());

    }

    @Test
    void getUserDtoResponse() {

        Mockito.when(userRepository.findById(user1.getId())).thenReturn(Optional.of(user1));
        Mockito.when(userRepository.save(ArgumentMatchers.any(User.class))).then(AdditionalAnswers.returnsFirstArg());

        UserDtoResponse expected = userService.create(userDtoRequest1);
        expected.setId(user1.getId());
        UserDtoResponse actual = userService.getUserDtoResponse(user1.getId());

        Assertions.assertNotNull(actual);
        Assertions.assertEquals(expected.getId(), actual.getId());
        Assertions.assertEquals(expected.getName(), actual.getName());
        Assertions.assertEquals(expected.getEmail(), actual.getEmail());

    }

    @Test
    void getAll() {

        List<UserDtoResponse> expectedList;
        List<UserDtoResponse> actualList;

        Mockito
                .when(userRepository.findAll())
                .thenReturn(List.of(user1, user2));

        expectedList = List.of(UserMapper.toUserDtoResponse(user1), UserMapper.toUserDtoResponse(user2));
        actualList = userService.getAll();

        Assertions.assertEquals(expectedList, actualList);

    }

    @Test
    void delete() {

        userService.delete(user1.getId());

        Mockito.verify(userRepository, Mockito.times(1))
                .deleteById(user1.getId());
    }

}