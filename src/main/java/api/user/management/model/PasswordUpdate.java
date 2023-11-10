package api.user.management.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
@Setter
@Builder
public class PasswordUpdate {

  private String newPassword;
  private String currentPassword;
}
