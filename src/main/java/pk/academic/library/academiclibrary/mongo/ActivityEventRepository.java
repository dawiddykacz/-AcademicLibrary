package pk.academic.library.academiclibrary.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import pk.academic.library.academiclibrary.mongo.documents.ActivityEvent;
import java.util.List;

@Repository
public interface ActivityEventRepository extends MongoRepository<ActivityEvent, String> {
    List<ActivityEvent> findTop5ByReaderIdAndEventTypeOrderByTimestampDesc(String readerId, String eventType);
}