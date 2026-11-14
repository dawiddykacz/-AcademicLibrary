package pk.academic.library.academiclibrary;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;

@EnableJpaRepositories(basePackages = "pk.academic.library.academiclibrary.sql")
@EnableMongoRepositories(basePackages = "pk.academic.library.academiclibrary.mongo")
@EnableNeo4jRepositories(basePackages = "pk.academic.library.academiclibrary.neo4j")
@SpringBootApplication
public class AcademicLibraryApplication {

    public static void main(String[] args) {
        SpringApplication.run(AcademicLibraryApplication.class, args);
    }

}
