package api.user.management.model.collection;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
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
@JsonDeserialize(builder = User.UserBuilder.class)
@JsonSerialize(as = User.class)
public class User {

  @Id
  private ObjectId id;
  private String email;
  private String userName;
  @JsonIgnore
  private String password;
}
