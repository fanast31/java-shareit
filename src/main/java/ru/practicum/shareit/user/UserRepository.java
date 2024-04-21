package ru.practicum.shareit.user;

import java.util.List;

public interface UserRepository {
    User findById(long userId);
    List<User> getAll();
    User save(User user);
    void delete(long userId);
}
