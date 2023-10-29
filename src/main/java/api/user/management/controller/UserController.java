package api.user.management.controller;

import api.user.management.model.Login;
import api.user.management.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@AllArgsConstructor
public class UserController {
    private UserService userService;

    @PostMapping
    public ResponseEntity<String> login(@RequestBody Login login) {
        log.info("login controller");

        return userService.authenticateUser(login);
    }
}
