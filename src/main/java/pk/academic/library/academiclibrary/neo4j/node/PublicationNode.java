package pk.academic.library.academiclibrary.neo4j.node;

import org.springframework.data.neo4j.core.schema.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Node("Publication")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PublicationNode {
    @Id
    private String id;

    private String title;

    @Relationship(type = "CITED_BY", direction = Relationship.Direction.INCOMING)
    private Set<PublicationNode> citedBy;

    @Relationship(type = "AUTHORED_BY", direction = Relationship.Direction.OUTGOING)
    private Set<AuthorNode> authors;
}