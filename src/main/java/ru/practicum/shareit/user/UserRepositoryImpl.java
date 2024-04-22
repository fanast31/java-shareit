package ru.practicum.shareit.user;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.ValidationException;

import java.util.*;

@Component
public class UserRepositoryImpl implements UserRepository {

    private long id = 1L;
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public User findById(long userId) {
        return users.get(userId);
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User save(User user) throws ValidationException {
        User userDuplicateEmail =
                users.values().stream()
                        .filter(user1 -> user1.getEmail().equals(user.getEmail()))
                        .filter(user1 -> user.getId() == null || !user1.getId().equals(user.getId()))
                        .findFirst()
                        .orElse(null);
        if (userDuplicateEmail != null) {
            throw new ValidationException("Duplicate email");
        }
        if (user.getId() == null) {
            user.setId(id++);
        }
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public void delete(long userId) {
        users.remove(userId);
    }

}
