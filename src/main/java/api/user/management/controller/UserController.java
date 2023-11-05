package api.user.management.controller;

import api.user.management.model.Login;
import api.user.management.model.Registration;
import api.user.management.model.RequestResponse;
import api.user.management.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/v1/users")
@CrossOrigin(origins = "localhost:3000")
public class UserController {
    private UserService userService;

    @GetMapping
    public ResponseEntity<RequestResponse> getUsers(@RequestParam String userid) {
        log.info("getUsers controller");
        return userService.getUser(userid);
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> login(@RequestBody Login userLogin) {
        log.info("login controller");
        return userService.authenticateUser(userLogin);
    }

    @PostMapping("/register")
    public ResponseEntity<RequestResponse> register(@RequestBody Registration registration) {
        log.info("register controller");
        return userService.registerUser(registration);
    }
}


