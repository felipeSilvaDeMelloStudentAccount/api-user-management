package api.user.management.service;

import api.user.management.model.Login;
import api.user.management.model.User;
import api.user.management.repository.UserRepository;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder encoder;

    public static String EMAIL = "testuser@example.com";

    public static String PASSWORD = "password123";


    @Test
    void testAuthenticateUser() {
        // Define mock behavior for UserRepository
        when(userRepository.findByEmail(EMAIL))
                .thenReturn(Optional.of(User.builder().email(EMAIL)
                        .password(PASSWORD).build()));

        Login login = Login.builder().email(EMAIL).password(PASSWORD).build();

        // Define mock behavior for PasswordEncoder
        when(encoder.matches(anyString(), anyString())).thenReturn(true);


        ResponseEntity<String> response = userService.authenticateUser(login);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(EMAIL, response.getBody());

        // Verify that the encoder.matches method was called
        verify(encoder).matches(anyString(), anyString());
    }

    @Test
    void testLoadUserByUsername() {
        when(userRepository.findByEmail("testuser@example.com"))
                .thenReturn(Optional.of(User.builder().email("testuser@example.com")
                        .password("password123").build()));
        UserDetails userDetails = userService.loadUserByUsername("testuser@example.com");
        assertNotNull(userDetails);
        assertEquals("testuser@example.com", userDetails.getUsername());
        assertTrue(userDetails.getAuthorities().isEmpty());
    }

    @Test
    void testLoadUserByUsernameNotFound() {
        when(userRepository.findByEmail("nonexistent@example.com"))
                .thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class,
                () -> userService.loadUserByUsername("nonexistent@example.com"));
    }

    @Test
    void testAuthenticateUserSuccess() {
        when(encoder.matches(anyString(), anyString())).thenReturn(true);
        when(userRepository.findByEmail(EMAIL))
                .thenReturn(Optional.of(User.builder().email(EMAIL)
                        .password(PASSWORD).build()));

        Login login = new Login("testuser@example.com", "password123");
        ResponseEntity<String> response = userService.authenticateUser(login);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void testAuthenticateUserUserNotFound() {
        Login login = new Login("nonexistent@example.com", "password123");
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());
        ResponseEntity<String> response = userService.authenticateUser(login);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Email nonexistent@example.com not found", response.getBody());
    }

    @Test
    void testAuthenticateUserIncorrectPassword() {
        when(userRepository.findByEmail(EMAIL))
                .thenReturn(Optional.of(User.builder().email(EMAIL)
                        .password(PASSWORD).build()));
        // Define mock behavior for PasswordEncoder
        when(encoder.matches(anyString(), anyString())).thenReturn(false);

        Login login = new Login("testuser@example.com", "wrongPassword");
        ResponseEntity<String> response = userService.authenticateUser(login);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNull(response.getBody());
    }
}