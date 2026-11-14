package pk.academic.library.academiclibrary.mongo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import pk.academic.library.academiclibrary.mongo.documents.Publication;

@Repository
public interface PublicationRepository extends MongoRepository<Publication, String> {
}