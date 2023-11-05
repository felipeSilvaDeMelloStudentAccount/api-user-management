package api.user.management.service;

import api.user.management.model.Login;
import api.user.management.model.Registration;
import api.user.management.model.RequestResponse;
import api.user.management.model.collection.User;
import api.user.management.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
public class UserService implements UserDetailsService {

    private UserRepository userRepository;
    private PasswordEncoder encoder;
    private JwtTokenService jwtTokenService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            log.debug("Email {} not found", email);
            throw new UsernameNotFoundException("Email " + email + " not found");
        }
        return new org.springframework.security.core.userdetails.User(
                user.get().getEmail(),
                user.get().getPassword(),
                Collections.emptyList()
        );
    }

    public ResponseEntity<Map<String, String>> authenticateUser(Login userLogin) {
        UserDetails userDetails;
        Map<String, String> response = new HashMap<>();

        try {
            userDetails = loadUserByUsername(userLogin.getEmail());
        } catch (UsernameNotFoundException e) {
            log.debug("Email {} not found", userLogin.getEmail());
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        if (!encoder.matches(userLogin.getPassword(), userDetails.getPassword())) {
            log.debug("Invalid password for email {}", userLogin.getEmail());
            response.put("message", "Incorrect Password");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        String jwtToken = jwtTokenService.generateJwtToken(userDetails.getUsername());
        log.debug("JWT token generated for email {} is {}", userLogin.getEmail(), jwtToken);
        response.put("jwtToken", jwtToken);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Transactional
    public ResponseEntity<RequestResponse> registerUser(Registration registration) {
        log.info("register user");
        RequestResponse response = new RequestResponse();
        try {
            Optional<User> user = userRepository.findByEmail(registration.getEmail());
            if (user.isPresent()) {
                log.debug("Email {} already exists", registration.getEmail());
                response.setMessage("Email already exists");
                response.setHttpStatus(HttpStatus.BAD_REQUEST);
            } else {
                // Encode password
                String encodedPassword = encoder.encode(registration.getPassword());
                // Create a new User
                User userProfile = User.builder()
                        .email(registration.getEmail())
                        .password(encodedPassword)
                        .userName(registration.getUserName())
                        .build();
                userRepository.save(userProfile);
                response.setMessage("User registered successfully");
                response.setHttpStatus(HttpStatus.CREATED);
            }
        } catch (DataAccessException e) {
            log.debug("Error while saving user: {}", e.getMessage());
            response.setMessage("Database error: " + e.getMessage());
            response.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            log.debug("Error while saving user: {}", e.getMessage());
            response.setMessage("Error while saving user");
            response.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return ResponseEntity.status(response.getHttpStatus()).body(response);
    }

    public ResponseEntity<RequestResponse> getUser(String userid) {
        log.info("getUser service");
        RequestResponse response = new RequestResponse();

        try {
            Optional<User> user = userRepository.findById(userid);
            if (user.isEmpty()) {
                log.debug("User {} not found", userid);
                response.setMessage("User not found");
                response.setHttpStatus(HttpStatus.NOT_FOUND);
            } else {
                response.setMessage("User found");
                response.setHttpStatus(HttpStatus.OK);
            }
        } catch (DataAccessException e) {
            log.debug("Error while getting user: {}", e.getMessage());
            response.setMessage("Database error: " + e.getMessage());
            response.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            log.debug("Error while getting user: {}", e.getMessage());
            response.setMessage("Error while getting user");
            response.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return ResponseEntity.status(response.getHttpStatus()).body(response);
    }
}
