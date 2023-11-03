package api.user.management.repository;

import api.user.management.model.UserProfile;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<UserProfile, String> {

    @Query("{'userAuthLogin.email': ?0}")
    Optional<UserProfile> findByEmail(String email);

}