package pk.academic.library.academiclibrary.mongo.documents;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.*;
import java.util.List;
import java.util.Map;

@Document(collection = "publications")
@Data @NoArgsConstructor @AllArgsConstructor
public class Publication {
    @Id
    private String id;

    private String title;
    private String type;
    private String isbn;

    private Map<String, Object> details;

    private List<Review> reviews;
}
