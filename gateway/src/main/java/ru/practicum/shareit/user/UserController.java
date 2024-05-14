package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDtoRequest;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {

    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody UserDtoRequest userDtoRequest) {
        return userClient.createUser(userDtoRequest);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> update(@PathVariable long userId, @RequestBody UserDtoRequest userDtoRequest) {
        return userClient.updateUser(userId, userDtoRequest);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> get(@PathVariable long userId) {
        return userClient.getUserById(userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAll() {
        return userClient.getAllUsers();
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> delete(@PathVariable long userId) {
        userClient.deleteUser(userId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

}
