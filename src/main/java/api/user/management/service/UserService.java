package api.user.management.service;

import api.user.management.model.Login;
import api.user.management.model.PasswordUpdate;
import api.user.management.model.Registration;
import api.user.management.model.RequestResponse;
import api.user.management.model.collection.User;
import api.user.management.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
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

    public static final String USER_NOT_FOUND = "User {} not found";
    public static final String USER_NOT_FOUND_MESSAGE = "User not found";
    public static final String DATABASE_ERROR = "Database error: ";
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
            response.setMessage("Database error: {}" + e.getMessage());
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
            ObjectId objectId = new ObjectId(userid);
            Optional<User> user = userRepository.findById(objectId);
            if (user.isEmpty()) {
                log.debug(USER_NOT_FOUND, userid);
                response.setMessage(USER_NOT_FOUND_MESSAGE);
                response.setHttpStatus(HttpStatus.NO_CONTENT);
            } else {
                log.debug("User {} found", userid);
                response.setMessage("User found");
                response.setHttpStatus(HttpStatus.OK);
            }
        } catch (DataAccessException e) {
            log.debug("Error while getting user: {}", e.getMessage());
            response.setMessage(DATABASE_ERROR + e.getMessage());
            response.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            log.debug("Error while getting user: {}", e.getMessage());
            response.setMessage("Error while getting user");
            response.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return ResponseEntity.status(response.getHttpStatus()).body(response);
    }

    public ResponseEntity<RequestResponse> updateUserPassword(String userid, PasswordUpdate passwordUpdate) {
        log.info("updateUser service");
        RequestResponse response = new RequestResponse();
        try {
            Optional<User> optionalUser = userRepository.findById(userid);
            if (optionalUser.isEmpty()) {
                log.debug(USER_NOT_FOUND, userid);
                response.setMessage(USER_NOT_FOUND_MESSAGE);
                response.setHttpStatus(HttpStatus.NOT_FOUND);
            }
            if (optionalUser.isPresent()) {
                if (!encoder.matches(passwordUpdate.getCurrentPassword(), optionalUser.get().getPassword())) {
                    log.debug("Invalid password for userid {}", userid);
                    response.setMessage("Incorrect Password");
                    response.setHttpStatus(HttpStatus.UNAUTHORIZED);
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
                } else {
                    // Encode password
                    String encodedPassword = encoder.encode(passwordUpdate.getNewPassword());
                    // Create a new User
                    optionalUser.get().setPassword(encodedPassword);
                    userRepository.save(optionalUser.get());
                    response.setMessage("Password updated successfully");
                    response.setHttpStatus(HttpStatus.OK);
                }
            }
        } catch (DataAccessException e) {
            log.debug("Error while updating user: {}", e.getMessage());
            response.setMessage(DATABASE_ERROR + e.getMessage());
            response.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            log.debug("Error while updating user: {}", e.getMessage());
            response.setMessage("Error while updating user");
            response.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return ResponseEntity.status(response.getHttpStatus()).body(response);
    }

    public ResponseEntity<RequestResponse> deleteUser(String userid) {
        log.info("deleteUser service");
        RequestResponse response = new RequestResponse();
        try {
            ObjectId objectId = new ObjectId(userid);
            Optional<User> user = userRepository.findById(objectId);
            if (user.isEmpty()) {
                log.debug(USER_NOT_FOUND, userid);
                response.setMessage(USER_NOT_FOUND_MESSAGE);
                response.setHttpStatus(HttpStatus.NOT_FOUND);
            } else {
                userRepository.delete(user.get());
                response.setMessage("User deleted successfully");
                response.setHttpStatus(HttpStatus.OK);
            }
        } catch (DataAccessException e) {
            log.debug("Error while deleting user: {}", e.getMessage());
            response.setMessage(DATABASE_ERROR + e.getMessage());
            response.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            log.debug("Error while deleting user: {}", e.getMessage());
            response.setMessage("Error while deleting user");
            response.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return ResponseEntity.status(response.getHttpStatus()).body(response);
    }
}
