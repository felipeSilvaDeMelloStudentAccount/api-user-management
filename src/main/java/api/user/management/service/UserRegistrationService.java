package api.user.management.service;

import static api.user.management.utils.Constants.DATABASE_ERROR;
import static api.user.management.utils.PasswordPolicyValidator.validatePassword;

import api.user.management.model.Error;
import api.user.management.model.Registration;
import api.user.management.model.collection.User;
import api.user.management.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.lang.Collections;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@AllArgsConstructor
public class UserRegistrationService {


  private UserRepository userRepository;
  private PasswordEncoder encoder;
  private ObjectMapper objectMapper;


  @Transactional
  public ResponseEntity<String> registerUser(Registration registration)
      throws JsonProcessingException {
    log.info("register user");
    try {
      Optional<User> user = userRepository.findByEmail(registration.getEmail());
      if (user.isPresent()) {
        log.debug("Email {} already exists", registration.getEmail());
        Error error = Error.builder()
            .cause("Email")
            .message("Email already exists")
            .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(objectMapper.writeValueAsString(error));
      } else {

        if (!registration.getPassword().equals(registration.getConfirmPassword())) {
          log.debug("Password {}, Confirm Password {}, does not match", registration.getPassword(),
              registration.getConfirmPassword());
          Error error = Error.builder()
              .cause("Password")
              .message("Passwords not matching")
              .build();
          return ResponseEntity.status(HttpStatus.BAD_REQUEST)
              .body(objectMapper.writeValueAsString(error));
        }
        List<Error> passwordErrors = validatePassword(registration.getPassword());
        if (!Collections.isEmpty(passwordErrors)) {
          return ResponseEntity.status(HttpStatus.BAD_REQUEST)
              .body(objectMapper.writeValueAsString(passwordErrors));
        }

        // Encode password
        String encodedPassword = encoder.encode(registration.getPassword());
        // Create a new User
        User userProfile = User.builder()
            .email(registration.getEmail())
            .password(encodedPassword)
            .userName(registration.getUserName())
            .build();
        userRepository.save(userProfile);
        log.debug("User {} registered successfully", registration.getEmail());
        //return the body in JSON format
        String response = "{\"message\":\"User registered successfully\"}";
        return ResponseEntity.status(HttpStatus.OK).body(objectMapper.writeValueAsString(response));
      }
    } catch (DataAccessException e) {
      log.debug("Error while saving user: {}", e.getMessage());
      Error error = Error.builder()
          .cause(DATABASE_ERROR)
          .message(e.getMessage())
          .build();
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(objectMapper.writeValueAsString(error));

    } catch (Exception e) {
      Error error = Error.builder()
          .cause("Server error")
          .message(e.getMessage())
          .build();
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(objectMapper.writeValueAsString(error));
    }
  }


}
