package api.user.management.utils;

import static api.user.management.utils.Constants.PASSWORD_VALIDATION_ERROR;

import api.user.management.model.Error;
import java.util.ArrayList;
import java.util.List;

public class PasswordPolicyValidator {

  private PasswordPolicyValidator() {
    throw new IllegalStateException("PasswordPolicyValidator class");
  }

  public static List<Error> validatePassword(String password) {
    List<Error> errors = new ArrayList<>();
    if (password == null || password.isEmpty()) {
      errors.add(Error.builder().cause(PASSWORD_VALIDATION_ERROR)
          .message("Password cannot be empty").build());
      return errors;
    }
    if (password.length() < 8) {
      errors.add(Error.builder().cause(PASSWORD_VALIDATION_ERROR)
          .message("Password must be at least 8 characters long").build());
    }

    // Check for at least one uppercase letter
    if (!containsUppercase(password)) {
      errors.add(Error.builder().cause(PASSWORD_VALIDATION_ERROR)
          .message("Password must contain at least one uppercase letter").build());
    }

    // Check for at least one lowercase letter
    if (!containsLowercase(password)) {
      errors.add(Error.builder().cause(PASSWORD_VALIDATION_ERROR)
          .message("Password must contain at least one lowercase letter").build());

    }
    // Check for at least one symbol (non-alphanumeric character)
    if (!containsSymbol(password)) {
      errors.add(Error.builder().cause(PASSWORD_VALIDATION_ERROR)
          .message("Password must contain at least one symbol").build());
    }
    return errors;
  }

  private static boolean containsUppercase(String password) {
    return !password.equals(password.toLowerCase());
  }

  private static boolean containsLowercase(String password) {
    return !password.equals(password.toUpperCase());
  }

  private static boolean containsSymbol(String password) {
    return password.matches(".*[^a-zA-Z0-9].*");
  }
}
