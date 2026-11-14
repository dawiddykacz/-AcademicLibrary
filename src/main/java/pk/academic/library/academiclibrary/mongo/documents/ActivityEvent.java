package pk.academic.library.academiclibrary.mongo.documents;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Map;

@Document(collection = "events")
@Data @NoArgsConstructor @AllArgsConstructor
public class ActivityEvent {
    @Id
    private String id;
    private String readerId;
    private String publicationId;
    private String eventType; // np. "VIEW", "SEARCH", "BORROW"

    @Indexed(expireAfter = "7776000")
    private Instant timestamp;

    private Map<String, Object> metadata;
}