package pk.academic.library.academiclibrary;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.Neo4jContainer;
import org.testcontainers.containers.PostgreSQLContainer;

@SpringBootTest
public abstract class BaseIntegrationTest {
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("library_test")
            .withUsername("test")
            .withPassword("test");

    static MongoDBContainer mongo = new MongoDBContainer("mongo:6.0");

    static Neo4jContainer<?> neo4j = new Neo4jContainer<>("neo4j:5.9")
            .withAdminPassword("password");

    static {
        postgres.start();
        mongo.start();
        neo4j.start();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);

        registry.add("spring.data.mongodb.uri", () ->
                String.format("mongodb://%s:%d/library_test", mongo.getHost(), mongo.getFirstMappedPort())
        );

        registry.add("spring.neo4j.uri", neo4j::getBoltUrl);
        registry.add("spring.neo4j.authentication.username", () -> "neo4j");
        registry.add("spring.neo4j.authentication.password", () -> "password");
    }
}