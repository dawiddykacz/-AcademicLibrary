package pk.academic.library.academiclibrary.neo4j;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import pk.academic.library.academiclibrary.neo4j.node.PublicationNode;

public interface PublicationNodeRepository extends Neo4jRepository<PublicationNode, String> {
}