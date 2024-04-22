package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.DataNotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;

    @Override
    public User create(User user) throws ValidationException {
        return userRepository.save(user);
    }

    @Override
    public User update(long userId, User user) throws DataNotFoundException {

        User userDB = userRepository.findById(userId);
        if (userDB == null) {
            throw new DataNotFoundException("User not found");
        }

        user.setId(userDB.getId());
        if (user.getName() == null) {
            user.setName(userDB.getName());
        }
        if (user.getEmail() == null) {
            user.setEmail(userDB.getEmail());
        }

        return userRepository.save(user);
    }

    @Override
    public User get(long userId) throws DataNotFoundException {
        User user = userRepository.findById(userId);
        if (user == null) {
            throw new DataNotFoundException("User not found");
        }
        return user;
    }

    @Override
    public List<User> getAll() {
        return userRepository.getAll();
    }

    @Override
    public void delete(long userId) {
        User user = userRepository.findById(userId);
        if (user == null) {
            throw new DataNotFoundException("User not found");
        }
        userRepository.delete(user.getId());
    }

}
