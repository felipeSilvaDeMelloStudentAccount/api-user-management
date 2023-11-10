package api.user.management.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecSecurityConfig {


  /**
   * BCrypt, however, will internally generate a random salt instead. This is important to
   * understand because it means that each call will have a different result, so we only need to
   * encode the password once.
   *
   * @return BCryptPasswordEncoder
   */
  @Bean
  public PasswordEncoder encoder() {
    return new BCryptPasswordEncoder();
  }
}
