package nosql.mongo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

/**
 * Rucna Spring konfiguracia (bez Spring Boot) - podobne ako DaoFactory
 * rucne vytvaral JdbcTemplate, tu rucne vytvarame MongoClient a zapneme
 * Spring Data Mongo repozitare (CrudRepository).
 *
 * Predpokladame lokalny MongoDB v dockeri z docker-compose.yml (localhost:27017,
 * bez autentifikacie).
 */
@Configuration
@EnableMongoRepositories(basePackages = "nosql.mongo.repository")
public class MongoConfig extends AbstractMongoClientConfiguration {

    public static final String CONNECTION_STRING = "mongodb://localhost:27017";
    public static final String DATABASE_NAME = "nosql-students";

    @Override
    public String getDatabaseName() {
        return DATABASE_NAME;
    }

    @Override
    @Bean
    public MongoClient mongoClient() {
        ConnectionString connectionString = new ConnectionString(CONNECTION_STRING);
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .build();
        return MongoClients.create(settings);
    }
}
