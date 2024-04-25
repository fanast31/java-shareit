package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserDto> create(@Valid @RequestBody UserDto userDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                UserMapper.toUserDto(userService.create(UserMapper.toUser(userDto)))
        );
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<UserDto> update(@PathVariable long userId, @RequestBody UserDto userDto) {
        return ResponseEntity.status(HttpStatus.OK).body(
                UserMapper.toUserDto(userService.update(userId, UserMapper.toUser(userDto)))
        );
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> get(@PathVariable long userId) {
        return ResponseEntity.status(HttpStatus.OK).body(UserMapper.toUserDto(userService.get(userId)));
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> getAll() {
        return ResponseEntity.status(HttpStatus.OK).body(
                userService.getAll().stream()
                        .map(UserMapper::toUserDto)
                        .collect(Collectors.toList()));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> delete(@PathVariable long userId) {
        userService.delete(userId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

}
