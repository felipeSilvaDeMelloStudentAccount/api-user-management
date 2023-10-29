package api.user.management.service;

import api.user.management.model.Login;
import api.user.management.model.User;
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
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            throw new UsernameNotFoundException("Email " + email + " not found");
        }
        return new org.springframework.security.core.userdetails.User(
                user.get().getEmail(),
                user.get().getPassword(), // Assuming the password is already hashed
                Collections.emptyList()
        );
    }

    public ResponseEntity<String> authenticateUser(Login login) {
        UserDetails userDetails;
        try {
            userDetails = loadUserByUsername(login.getEmail());
        } catch (UsernameNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
        if (!encoder.matches(login.getPassword(), userDetails.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String jwtToken = generateJwtToken(login.getEmail());
        return ResponseEntity.ok(jwtToken);
    }

    private String generateJwtToken(String username) {
        // TODO - Implement JWT token service in here
        // You can use libraries like jwt for this purpose
        return username;
    }
}
