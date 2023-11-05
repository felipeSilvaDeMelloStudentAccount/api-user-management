package api.user.management.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Setter
@Getter
public class RequestResponse {
    private String message;
    private HttpStatus httpStatus;
}
