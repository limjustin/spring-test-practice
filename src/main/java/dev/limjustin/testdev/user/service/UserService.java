package dev.limjustin.testdev.user.service;

import dev.limjustin.testdev.user.domain.User;
import dev.limjustin.testdev.user.domain.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;

    public User join(User user) {
        if (user.getNickname().length() > 15)
            throw new IllegalArgumentException("Nickname too long");
        return userRepository.save(user);
    }
}
