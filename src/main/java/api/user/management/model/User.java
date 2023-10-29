package api.user.management.model;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "users")
@Getter
@Setter
@Builder
public class User {

    @Id
    private String id;
    private String email;
    private String password;
}
