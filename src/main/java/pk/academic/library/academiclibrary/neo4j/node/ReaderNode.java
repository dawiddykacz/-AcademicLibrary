package pk.academic.library.academiclibrary.neo4j.node;
import org.springframework.data.neo4j.core.schema.*;
import lombok.*;
import java.util.Set;

@Node("Reader")
@Data @NoArgsConstructor @AllArgsConstructor
public class ReaderNode {
    @Id
    private String id; // Wspólny ID

    private String name;

    @Relationship(type = "BORROWED", direction = Relationship.Direction.OUTGOING)
    private Set<PublicationNode> borrowedPublications;
}