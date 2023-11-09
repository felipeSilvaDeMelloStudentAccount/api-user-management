package api.user.management.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Registration {

  private String email;
  private String userName;
  private String password;
}