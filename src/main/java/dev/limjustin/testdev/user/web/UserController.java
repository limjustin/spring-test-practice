package dev.limjustin.testdev.user.web;

import dev.limjustin.testdev.user.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
public class UserController {

    private final UserService userService;
}
