package api.user.management.model.collection;


import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "users")
@Getter
@Builder
public class User {

    @Id
    private String id;
    private String email;
    private String userName;
    private String password;
}
