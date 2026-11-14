package pk.academic.library.academiclibrary.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableJpaRepositories(
        basePackages = "pk.academic.library.academiclibrary.sql",
        transactionManagerRef = "transactionManager"
)
@EnableNeo4jRepositories(
        basePackages = "pk.academic.library.academiclibrary.neo4j",
        transactionManagerRef = "neo4jTransactionManager"
)
@EnableMongoRepositories(
        basePackages = "pk.academic.library.academiclibrary.mongo"
)
public class DatabaseConfig {
}