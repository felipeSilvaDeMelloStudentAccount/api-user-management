package api.user.management.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;

@Configuration
public class MongoDBConfig {


    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public MongoTemplate mongoTemplate(@Value("${spring.data.mongodb.uri}") String connectionString) {
        return new MongoTemplate(new SimpleMongoClientDatabaseFactory(connectionString));
    }
}
