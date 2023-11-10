package api.user.management.service;

import api.user.management.model.ErrorClass;
import api.user.management.model.Login;
import api.user.management.model.collection.User;
import api.user.management.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class UserAuthenticationService {

  private UserRepository userRepository;
  private PasswordEncoder encoder;
  private JwtTokenService jwtTokenService;
  private ObjectMapper objectMapper;

  public ResponseEntity<String> authenticateUser(Login userLogin) throws JsonProcessingException {
    String email = userLogin.getEmail();
    String password = userLogin.getPassword();
    Optional<User> user = userRepository.findByEmail(email);
    if (user.isEmpty()) {
      log.debug("Email {} not found", email);
      ErrorClass errorClass = ErrorClass.builder()
          .status(HttpStatus.BAD_REQUEST)
          .cause("Email not found")
          .message("Email " + email + " not found")
          .build();
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(objectMapper.writeValueAsString(errorClass));
    }
    if (!encoder.matches(password, user.get().getPassword())) {
      log.debug("Invalid password for email {}", email);
      ErrorClass errorClass = ErrorClass.builder()
          .status(HttpStatus.BAD_REQUEST)
          .cause("Password")
          .message("Password does not match")
          .build();
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(objectMapper.writeValueAsString(errorClass));
    }
    String jwtToken = jwtTokenService.generateJwtToken(user.get());
    log.debug("JWT token generated for email {} is {}", email, jwtToken);
    // Return JWT token in JSON response
    String response = "{\"jwtToken\":\"" + jwtToken + "\"}";
    return ResponseEntity.status(HttpStatus.OK).body(response);

  }
}
