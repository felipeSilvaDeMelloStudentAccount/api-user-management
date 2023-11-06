package api.user.management.model;

import lombok.*;
import org.springframework.http.HttpStatus;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RequestResponse {
    private String message;
    private HttpStatus httpStatus;
}
