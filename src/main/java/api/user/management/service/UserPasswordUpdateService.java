package api.user.management.service;

import static api.user.management.utils.Constants.DATABASE_ERROR;
import static api.user.management.utils.Constants.USER_NOT_FOUND;
import static api.user.management.utils.Constants.USER_NOT_FOUND_MESSAGE;
import static api.user.management.utils.PasswordPolicyValidator.validatePassword;

import api.user.management.model.Error;
import api.user.management.model.PasswordUpdate;
import api.user.management.model.RequestResponse;
import api.user.management.model.collection.User;
import api.user.management.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.lang.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class UserPasswordUpdateService {

  private UserRepository userRepository;
  private ObjectMapper objectMapper;
  private PasswordEncoder encoder;


  public ResponseEntity<String> updateUserPassword(String userid, PasswordUpdate passwordUpdate)
      throws JsonProcessingException {
    log.info("updateUser service");
    RequestResponse response = new RequestResponse();
    try {
      Optional<User> optionalUser = userRepository.findById(userid);
      if (optionalUser.isEmpty()) {
        log.debug(USER_NOT_FOUND, userid);
        Error error = Error.builder()
            .cause("User not found")
            .message(USER_NOT_FOUND_MESSAGE)
            .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(objectMapper.writeValueAsString(error));
      }
      List<Error> passwordErrors = validatePassword(passwordUpdate.getNewPassword());
      if (!Collections.isEmpty(passwordErrors)) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(objectMapper.writeValueAsString(passwordErrors));
      }

      if (!encoder.matches(passwordUpdate.getCurrentPassword(), optionalUser.get().getPassword())) {
        log.debug("Invalid password for userid {}", userid);
        response.setMessage("Incorrect Password");
        Error error = Error.builder()
            .cause("Incorrect Password")
            .message("Current Password does not match old password")
            .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(objectMapper.writeValueAsString(error));
      } else {
        // Encode password
        String encodedPassword = encoder.encode(passwordUpdate.getNewPassword());
        optionalUser.get().setPassword(encodedPassword);
        userRepository.save(optionalUser.get());
        Map<String, String> map = new HashMap<>();
        map.put("message", "Password updated successfully");
        return ResponseEntity.status(HttpStatus.OK).body(objectMapper.writeValueAsString(map));

      }
    } catch (DataAccessException e) {
      log.debug("Error while updating user: {}", e.getMessage());
      Error error = Error.builder()
          .cause(DATABASE_ERROR)
          .message(e.getMessage())
          .build();
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(objectMapper.writeValueAsString(error));

    } catch (Exception e) {
      log.debug("Error while updating user: {}", e.getMessage());
      Error error = Error.builder()
          .cause("Server error")
          .message(e.getMessage())
          .build();
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(objectMapper.writeValueAsString(error));
    }
  }

}
