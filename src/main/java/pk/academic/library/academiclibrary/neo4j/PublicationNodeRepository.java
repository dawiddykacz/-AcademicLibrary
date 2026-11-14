package pk.academic.library.academiclibrary.neo4j;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;
import pk.academic.library.academiclibrary.neo4j.node.PublicationNode;

import java.time.LocalDate;
import java.util.List;


@Repository
public interface PublicationNodeRepository extends Neo4jRepository<PublicationNode, String> {

    // Wymaganie Neo4j pkt 5 i Integracja: zapis przez MERGE z datą i liczbą dni, bez tworzenia duplikatów
    @Query("MATCH (r:Reader {id: $readerId}), (p:Publication {id: $pubId}) " +
            "MERGE (r)-[rel:BORROWED]->(p) " +
            "ON CREATE SET rel.borrowDate = $date, rel.days = $days")
    void mergeBorrowedRelationship(String readerId, String pubId, LocalDate date, int days);

    // Wymaganie Neo4j pkt 1: Rekomendacja "czytelnicy, którzy wypożyczyli X, wypożyczyli też..."
    @Query("MATCH (p:Publication {id: $pubId})<-[:BORROWED]-(r:Reader)-[:BORROWED]->(rec:Publication) " +
            "WHERE rec.id <> $pubId " +
            "RETURN rec, count(DISTINCT r) AS commonReaders " +
            "ORDER BY commonReaders DESC LIMIT 5")
    List<PublicationNode> findAlsoBorrowed(String pubId);

    // Rekomendacje dla profilu czytelnika (bez publikacji, które czytelnik już wypożyczył)
    @Query("MATCH (r:Reader {id: $readerId})-[:BORROWED]->(p:Publication)<-[:BORROWED]-(other:Reader)-[:BORROWED]->(rec:Publication) " +
            "WHERE NOT (r)-[:BORROWED]->(rec) " +
            "RETURN rec, count(DISTINCT other) AS score " +
            "ORDER BY score DESC LIMIT 5")
    List<PublicationNode> findRecommendationsForReader(String readerId);

    // Wymaganie Neo4j pkt 2 / Biblioteka: Powiązane lektury przez sieć cytowań (do 2 kroków)
    @Query("MATCH (p:Publication {id: $pubId})-[:CITED_BY*1..2]-(related:Publication) " +
            "WHERE related.id <> $pubId " +
            "RETURN DISTINCT related LIMIT 5")
    List<PublicationNode> findRelatedByCitations(String pubId);
}