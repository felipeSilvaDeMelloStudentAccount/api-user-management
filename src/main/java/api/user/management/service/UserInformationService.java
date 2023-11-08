package api.user.management.service;

import api.user.management.model.collection.User;
import api.user.management.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static api.user.management.utils.Constants.*;

@Service
@Slf4j
@AllArgsConstructor
public class UserInformationService {

    private UserRepository userRepository;
    private ObjectMapper objectMapper;


    public ResponseEntity<String> getUser(String userid) {
        log.info("getUser service");
        try {
            ObjectId objectId = new ObjectId(userid);
            Optional<User> user = userRepository.findById(objectId);
            if (user.isEmpty()) {
                log.debug(USER_NOT_FOUND, userid);

                return new ResponseEntity<>(USER_NOT_FOUND_MESSAGE, HttpStatus.NOT_FOUND);
            } else {
                log.debug("User {} found", userid);
                return new ResponseEntity<>(objectMapper.writeValueAsString(user), HttpStatus.OK);
            }
        } catch (DataAccessException e) {
            log.debug("Error while getting user: {}", e.getMessage());
            return new ResponseEntity<>(DATABASE_ERROR + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            log.debug("Error while getting user: {}", e.getMessage());
            return new ResponseEntity<>("Error while getting user", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<String> deleteUser(String userid) throws JsonProcessingException {
        log.info("deleteUser service");
        try {
            ObjectId objectId = new ObjectId(userid);
            Optional<User> user = userRepository.findById(objectId);
            if (user.isEmpty()) {
                log.debug(USER_NOT_FOUND, userid);
                return new ResponseEntity<>(USER_NOT_FOUND_MESSAGE, HttpStatus.NOT_FOUND);
            } else {
                userRepository.delete(user.get());
                Map<String, String> map = new HashMap<>();
                map.put("id", userid);
                map.put("message", "User deleted successfully");
                return new ResponseEntity<>(objectMapper.writeValueAsString(map), HttpStatus.OK);
            }
        } catch (DataAccessException e) {
            log.debug("Error while deleting user: {}", e.getMessage());
            Map<String, String> map = new HashMap<>();
            map.put("id", userid);
            map.put("message", DATABASE_ERROR);
            return new ResponseEntity<>(objectMapper.writeValueAsString(map), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            log.debug("Error while deleting user: {}", e.getMessage());
            Map<String, String> map = new HashMap<>();
            map.put("id", userid);
            map.put("message", "Server error");
            return new ResponseEntity<>(objectMapper.writeValueAsString(map), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
