package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDtoRequest;
import ru.practicum.shareit.user.dto.UserDtoResponse;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserDtoResponse> create(@Valid @RequestBody UserDtoRequest userDtoRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.create(userDtoRequest));
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<UserDtoResponse> update(@PathVariable long userId, @RequestBody UserDtoRequest userDtoRequest) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.update(userId, userDtoRequest));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDtoResponse> get(@PathVariable long userId) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getUserDtoResponse(userId));
    }

    @GetMapping
    public ResponseEntity<List<UserDtoResponse>> getAll() {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getAll());
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> delete(@PathVariable long userId) {
        userService.delete(userId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

}
