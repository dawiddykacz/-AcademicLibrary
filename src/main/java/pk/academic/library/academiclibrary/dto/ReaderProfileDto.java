package pk.academic.library.academiclibrary.dto;

import pk.academic.library.academiclibrary.mongo.documents.Publication;
import pk.academic.library.academiclibrary.neo4j.node.PublicationNode;
import pk.academic.library.academiclibrary.sql.entities.Borrowing;

import java.util.List;

public record ReaderProfileDto(
        String readerId,
        List<Borrowing> borrowingHistory,
        List<Publication> recentlyViewed,
        List<PublicationNode> recommendations
) {
}