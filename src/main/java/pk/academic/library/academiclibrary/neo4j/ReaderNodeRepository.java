package pk.academic.library.academiclibrary.neo4j;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;
import pk.academic.library.academiclibrary.neo4j.node.ReaderNode;

@Repository
public interface ReaderNodeRepository extends Neo4jRepository<ReaderNode, String> {}