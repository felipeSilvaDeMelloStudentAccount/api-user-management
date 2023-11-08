package api.user.management.controller;

import api.user.management.model.ErrorClass;
import api.user.management.model.Login;
import api.user.management.model.PasswordUpdate;
import api.user.management.model.Registration;
import api.user.management.service.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/v1/users")
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {

    private UserRegistrationService userRegistrationService;
    private UserInformationService userInformationService;
    private UserAuthenticationService userAuthenticationService;
    private UserPasswordUpdateService userPasswordUpdateService;
    private JwtTokenService jwtTokenService;
    private ObjectMapper objectMapper;

    @GetMapping("/{userid}")
    public ResponseEntity<String> getUsers(@RequestHeader("Authorization") String authorizationHeader, @PathVariable String userid) throws JsonProcessingException {
        log.info("getUsers controller");


        if (!jwtTokenService.validateToken(authorizationHeader)) {
            log.error("Invalid JwtToken");
            ErrorClass errorClass = ErrorClass.builder()
                    .status(HttpStatus.UNAUTHORIZED)
                    .cause("JwtToken")
                    .message("Invalid JwtToken")
                    .build();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(objectMapper.writeValueAsString(errorClass));
        }
        return userInformationService.getUser(userid);


    }

    @PatchMapping("/{userid}")
    public ResponseEntity<String> updatePassword(@PathVariable String userid, @RequestBody PasswordUpdate passwordUpdate) throws JsonProcessingException {
        log.info("getUsers controller");
        return userPasswordUpdateService.updateUserPassword(userid, passwordUpdate);
    }

    @DeleteMapping("/{userid}")
    public ResponseEntity<String> deleteUser(@PathVariable String userid) throws JsonProcessingException {
        log.info("deleteUser controller");
        return userInformationService.deleteUser(userid);
    }

    @PostMapping
    public ResponseEntity<String> login(@RequestBody Login userLogin) throws JsonProcessingException {
        log.info("login controller");
        return userAuthenticationService.authenticateUser(userLogin);
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@Validated @RequestBody Registration registration) throws JsonProcessingException {
        log.info("register controller");
        return userRegistrationService.registerUser(registration);
    }
}


