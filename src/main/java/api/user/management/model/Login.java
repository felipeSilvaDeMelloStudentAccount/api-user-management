package api.user.management.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Login {

  private String email;
  private String password;
}
