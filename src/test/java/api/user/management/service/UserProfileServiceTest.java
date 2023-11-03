package api.user.management.service;

import api.user.management.model.UserAuthLogin;
import api.user.management.model.UserProfile;
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
class UserProfileServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder encoder;

    @Mock
    private JwtTokenService jwtTokenService;

    public static String EMAIL = "testuser@example.com";

    public static String PASSWORD = "password123";


    @Test
    void testAuthenticateUser() {
        // Define mock behavior for UserRepository
        UserAuthLogin userAuthLogin = UserAuthLogin.builder().email(EMAIL).password(PASSWORD).build();
        when(userRepository.findByEmail(EMAIL))
                .thenReturn(Optional.of(UserProfile.builder().userAuthLogin(userAuthLogin).build()));
        when(jwtTokenService.generateJwtToken(EMAIL)).thenReturn("jwtToken");

        UserAuthLogin userLogin = UserAuthLogin.builder().email(EMAIL).password(PASSWORD).build();

        // Define mock behavior for PasswordEncoder
        when(encoder.matches(anyString(), anyString())).thenReturn(true);


        ResponseEntity<String> response = userService.authenticateUser(userLogin);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("jwtToken", response.getBody());

        // Verify that the encoder.matches method was called
        verify(encoder).matches(anyString(), anyString());
    }

    @Test
    void testLoadUserByUsername() {
        UserAuthLogin userAuthLogin = UserAuthLogin.builder().email("testuser@example.com")
                .password("password123").build();
        when(userRepository.findByEmail("testuser@example.com"))
                .thenReturn(Optional.of(UserProfile.builder().userAuthLogin(userAuthLogin).build()));
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
        UserAuthLogin userAuthLogin = UserAuthLogin.builder().email(EMAIL).password(PASSWORD).build();
        when(encoder.matches(anyString(), anyString())).thenReturn(true);
        when(userRepository.findByEmail(EMAIL))
                .thenReturn(Optional.of(UserProfile.builder().userAuthLogin(userAuthLogin).build()));
        when(jwtTokenService.generateJwtToken(EMAIL)).thenReturn("jwtToken");

        ResponseEntity<String> response = userService.authenticateUser(userAuthLogin);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void testAuthenticateUserUserNotFound() {
        UserAuthLogin userLogin = new UserAuthLogin("nonexistent@example.com", "password123");
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());
        ResponseEntity<String> response = userService.authenticateUser(userLogin);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Email nonexistent@example.com not found", response.getBody());
    }

    @Test
    void testAuthenticateUserIncorrectPassword() {
        UserAuthLogin userAuthLogin = UserAuthLogin.builder().email(EMAIL).password(PASSWORD).build();
        when(userRepository.findByEmail(EMAIL))
                .thenReturn(Optional.of(UserProfile.builder().userAuthLogin(userAuthLogin).build()));
        // Define mock behavior for PasswordEncoder
        when(encoder.matches(anyString(), anyString())).thenReturn(false);

        UserAuthLogin userLogin = new UserAuthLogin("testuser@example.com", "wrongPassword");
        ResponseEntity<String> response = userService.authenticateUser(userLogin);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNull(response.getBody());
    }
}