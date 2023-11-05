package api.user.management.controller;

import api.user.management.model.Login;
import api.user.management.model.Registration;
import api.user.management.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/v1/users")
public class UserController {
    private UserService userService;

    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping
    public ResponseEntity<String> login(@RequestBody Login userLogin) {
        log.info("login controller");
        return userService.authenticateUser(userLogin);
    }


    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody Registration registration) {
        log.info("register controller");
        return userService.registerUser(registration);
    }
}


