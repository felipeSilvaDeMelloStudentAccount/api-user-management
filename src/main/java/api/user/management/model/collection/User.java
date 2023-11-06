package api.user.management.model.collection;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "users")
@Getter
@Setter
@Builder
public class User {

    @Id
    private ObjectId id;
    private String email;
    private String userName;
    private String password;
}
