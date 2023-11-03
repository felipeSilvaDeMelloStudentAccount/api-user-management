package api.user.management.service;

import api.user.management.model.UserAuthLogin;
import api.user.management.model.UserProfile;
import api.user.management.repository.UserRepository;
import lombok.AllArgsConstructor;
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
@AllArgsConstructor
public class UserService implements UserDetailsService {

    private UserRepository userRepository;
    private PasswordEncoder encoder;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<UserProfile> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            throw new UsernameNotFoundException("Email " + email + " not found");
        }
        return new org.springframework.security.core.userdetails.User(
                user.get().getUserAuthLogin().getEmail(),
                user.get().getUserAuthLogin().getPassword(), // Assuming the password is already hashed
                Collections.emptyList()
        );
    }

    public ResponseEntity<String> authenticateUser(UserAuthLogin userLogin) {
        UserDetails userDetails;
        try {
            userDetails = loadUserByUsername(userLogin.getEmail());
        } catch (UsernameNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
        if (!encoder.matches(userLogin.getPassword(), userDetails.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String jwtToken = generateJwtToken(userLogin.getEmail());
        return ResponseEntity.ok(jwtToken);
    }

    public ResponseEntity<String> registerUser(UserAuthLogin userLogin) {
        Optional<UserProfile> user = userRepository.findByEmail(userLogin.getEmail());
        if (user.isPresent()) {
            return new ResponseEntity<>("Email already exists", HttpStatus.BAD_REQUEST);
        }
        try {
            String encodedPassword = encoder.encode(userLogin.getPassword());
            UserAuthLogin userAuthLogin = UserAuthLogin.builder().email(userLogin.getEmail())
                    .password(encodedPassword).build();
            UserProfile userProfile = UserProfile.builder().userAuthLogin(userAuthLogin).build();
            userRepository.save(userProfile);
        } catch (Exception e) {
            return new ResponseEntity<>("Error while saving user", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>("User registered successfully", HttpStatus.CREATED);
    }

    private String generateJwtToken(String username) {
        // TODO - Implement JWT token service in here
        // You can use libraries like jwt for this purpose
        return username;
    }
}
