package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.user.dto.UserDtoRequest;
import ru.practicum.shareit.user.dto.UserDtoResponse;
import ru.practicum.shareit.user.model.User;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserServiceIntegrationTest {

    private final UserService userService;

    private final UserRepository userRepository;

    User user;

    UserDtoRequest userDtoRequest;

    @BeforeEach
    void setUp() {

        user = User.builder()
                .name("user")
                .email("user@gmail.com")
                .build();

        userDtoRequest = userDtoRequest.builder()
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    @Test
    void createUser() {

        UserDtoResponse expected = userService.create(userDtoRequest);
        Optional<User> actualOptional = userRepository.findById(expected.getId());

        Assertions.assertTrue(actualOptional.isPresent());
        Assertions.assertEquals(expected, UserMapper.toUserDtoResponse(actualOptional.get()));

    }

    @Test
    void getUser() {

        User expected = userRepository.save(user);
        User actual = userService.getUser(expected.getId());

        Assertions.assertEquals(expected, actual);

    }

    @Test
    void updateUser() {

        UserDtoResponse expected = userService.create(userDtoRequest);
        expected.setName("newName");

        userDtoRequest.setName("newName");
        UserDtoResponse actual = userService.update(expected.getId(), userDtoRequest);

        Assertions.assertEquals(expected, actual);

    }

    @Test
    void getAllUsers() {

        List<UserDtoResponse> expected;
        List<UserDtoResponse> actual;

        User user2 = User.builder()
                .name("2")
                .email("2@mail.ru")
                .build();
        User user3 = User.builder()
                .name("3")
                .email("3@mail.ru")
                .build();
        expected = Stream.of(userRepository.save(user), userRepository.save(user2), userRepository.save(user3))
                .map(UserMapper::toUserDtoResponse).collect(Collectors.toList());

        actual = userService.getAll();

        Assertions.assertEquals(expected, actual);
    }


    @Test
    void deleteUser() {

        UserDtoResponse user2 = userService.create(userDtoRequest);
        userService.delete(user2.getId());
        Assertions.assertFalse(userRepository.existsById(user2.getId()));

    }

}
