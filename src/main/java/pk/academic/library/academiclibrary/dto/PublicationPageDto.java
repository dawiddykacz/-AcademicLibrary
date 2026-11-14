package pk.academic.library.academiclibrary.dto;

import pk.academic.library.academiclibrary.mongo.documents.Publication;
import pk.academic.library.academiclibrary.neo4j.node.PublicationNode;

import java.util.List;

public record PublicationPageDto(
        Publication publicationDetails,
        int availableCopiesCount,
        List<PublicationNode> citedPublications,
        List<PublicationNode> alsoBorrowed
) {}
