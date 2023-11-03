package api.user.management.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserRegistration {

    private UserAuthLogin userAuthLogin;
    private String userName;
    private String phoneNumber;
}