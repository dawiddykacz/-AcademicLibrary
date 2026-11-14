package pk.academic.library.academiclibrary.neo4j.node;

import org.springframework.data.neo4j.core.schema.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Node("Author")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthorNode {
    @Id
    @GeneratedValue
    private Long internalId;
    private String name;
}