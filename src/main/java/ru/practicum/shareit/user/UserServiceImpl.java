package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.DataNotFoundException;
import ru.practicum.shareit.user.dto.UserDtoRequest;
import ru.practicum.shareit.user.dto.UserDtoResponse;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDtoResponse create(UserDtoRequest userDtoRequest) {
        return UserMapper.toUserDtoResponse(userRepository.save(UserMapper.toUser(userDtoRequest)));
    }

    @Override
    public UserDtoResponse update(long userId, UserDtoRequest userDtoRequest) {

        User userDB = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("User not found"));

        User user = UserMapper.toUser(userDtoRequest);
        user.setId(userDB.getId());
        if (user.getName() == null) {
            user.setName(userDB.getName());
        }
        if (user.getEmail() == null) {
            user.setEmail(userDB.getEmail());
        }

        return UserMapper.toUserDtoResponse(userRepository.save(user));
    }

    @Override
    @Transactional(readOnly = true)
    public User getUser(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("User not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public UserDtoResponse getUserDtoResponse(long userId) {
        return UserMapper.toUserDtoResponse(getUser(userId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDtoResponse> getAll() {
        return userRepository.findAll().stream()
                .map(UserMapper::toUserDtoResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(long userId) {
        userRepository.deleteById(userId);
    }

}
