package api.user.management.service;

import api.user.management.model.Login;
import api.user.management.model.Registration;
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

import java.util.Collections;
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

    public ResponseEntity<String> authenticateUser(Login userLogin) {
        UserDetails userDetails;
        try {
            userDetails = loadUserByUsername(userLogin.getEmail());
        } catch (UsernameNotFoundException e) {
            log.debug("Email {} not found", userLogin.getEmail());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
        if (!encoder.matches(userLogin.getPassword(), userDetails.getPassword())) {
            log.debug("Invalid password for email {}", userLogin.getEmail());
            return new ResponseEntity<>("Incorrect Password", HttpStatus.UNAUTHORIZED);
        }
        String jwtToken = jwtTokenService.generateJwtToken(userDetails.getUsername());
        log.debug("JWT token generated for email {} is {}", userLogin.getEmail(), jwtToken);
        return ResponseEntity.ok(jwtToken);
    }

    public ResponseEntity<String> registerUser(Registration registration) {
        log.info("register user");
        try {
            Optional<User> user = userRepository.findByEmail(registration.getEmail());
            if (user.isPresent()) {
                log.debug("Email {} already exists", registration.getEmail());
                return new ResponseEntity<>("Email already exists", HttpStatus.BAD_REQUEST);
            }
            //Encode password
            String encodedPassword = encoder.encode(registration.getPassword());

            //Create a new User
            User userProfile = User.builder().email(registration.getEmail())
                    .password(encodedPassword).userName(registration.getUserName()).build();
            userRepository.save(userProfile);
            return new ResponseEntity<>("User registered successfully", HttpStatus.CREATED);
        } catch (DataAccessException e) {
            log.debug("Error while saving user: {}", e.getMessage());
            // Handle database-related exceptions (e.g., database connection issues)
            return new ResponseEntity<>("Database error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

        } catch (Exception e) {
            log.debug("Error while saving user: {}", e.getMessage());
            return new ResponseEntity<>("Error while saving user", HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

}
