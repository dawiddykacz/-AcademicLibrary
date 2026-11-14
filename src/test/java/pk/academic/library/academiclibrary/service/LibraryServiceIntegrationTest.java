package pk.academic.library.academiclibrary.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import pk.academic.library.academiclibrary.BaseIntegrationTest;
import pk.academic.library.academiclibrary.dto.PublicationPageDto;
import pk.academic.library.academiclibrary.mongo.ActivityEventRepository;
import pk.academic.library.academiclibrary.mongo.documents.ActivityEvent;
import pk.academic.library.academiclibrary.mongo.documents.Publication;
import pk.academic.library.academiclibrary.mongo.PublicationRepository;
import pk.academic.library.academiclibrary.neo4j.PublicationNodeRepository;
import pk.academic.library.academiclibrary.neo4j.ReaderNodeRepository;
import pk.academic.library.academiclibrary.neo4j.node.PublicationNode;
import pk.academic.library.academiclibrary.neo4j.node.ReaderNode;
import pk.academic.library.academiclibrary.sql.BookCopyRepository;
import pk.academic.library.academiclibrary.sql.BorrowingRepository;
import pk.academic.library.academiclibrary.sql.ReaderRepository;
import pk.academic.library.academiclibrary.sql.entities.BookCopy;
import pk.academic.library.academiclibrary.sql.entities.CopyStatus;
import pk.academic.library.academiclibrary.sql.entities.Reader;
import pk.academic.library.academiclibrary.sql.entities.Borrowing;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class LibraryServiceIntegrationTest extends BaseIntegrationTest {

    @Autowired private LibraryService libraryService;
    @Autowired private ReaderRepository readerRepository;
    @Autowired private BookCopyRepository bookCopyRepository;
    @Autowired private BorrowingRepository borrowingRepository;
    @Autowired private ReaderNodeRepository readerNodeRepository;
    @Autowired private PublicationNodeRepository publicationNodeRepository;

    @Autowired private ActivityEventRepository activityEventRepository;
    @Autowired private PublicationRepository publicationRepository;

    private String readerId;
    private String copyId;
    private String publicationId;

    @BeforeEach
    void setUp() {
        borrowingRepository.deleteAll();
        bookCopyRepository.deleteAll();
        readerRepository.deleteAll();
        readerNodeRepository.deleteAll();
        publicationNodeRepository.deleteAll();
        activityEventRepository.deleteAll();
        publicationRepository.deleteAll();

        readerId = UUID.randomUUID().toString();
        copyId = UUID.randomUUID().toString();
        publicationId = UUID.randomUUID().toString();

        Reader sqlReader = new Reader(readerId, "Jan", "Kowalski", "jan.kowalski@test.pl");
        readerRepository.save(sqlReader);

        BookCopy sqlCopy = new BookCopy(copyId, publicationId, "INV-999", CopyStatus.AVAILABLE);
        bookCopyRepository.save(sqlCopy);

        ReaderNode neo4jReader = new ReaderNode(readerId, "Jan Kowalski", new HashSet<>());
        readerNodeRepository.save(neo4jReader);

        PublicationNode neo4jPublication = new PublicationNode(publicationId, "Spring Boot Microservices", new HashSet<>(), new HashSet<>());
        publicationNodeRepository.save(neo4jPublication);

        Publication mongoPub = new Publication(publicationId, "Spring Boot Microservices", "BOOK", "123-456", Map.of(), List.of());
        publicationRepository.save(mongoPub);
    }

    @Test
    void shouldSuccessfullyBorrowBookAndSyncAcrossAllThreeDatabases() {
        assertEquals(0, borrowingRepository.count(), "Baza SQL powinna być pusta przed wypożyczeniem");

        libraryService.borrowBook(readerId, copyId);

        BookCopy updatedCopy = bookCopyRepository.findById(copyId).orElseThrow();
        assertEquals(CopyStatus.BORROWED, updatedCopy.getStatus());

        List<Borrowing> borrowings = borrowingRepository.findAll();
        assertEquals(1, borrowings.size());
        assertEquals(readerId, borrowings.get(0).getReader().getId());

        ReaderNode updatedReaderNode = readerNodeRepository.findById(readerId).orElseThrow();
        assertNotNull(updatedReaderNode.getBorrowedPublications());
        assertEquals(1, updatedReaderNode.getBorrowedPublications().size());
        assertEquals(publicationId, updatedReaderNode.getBorrowedPublications().iterator().next().getId());

        List<ActivityEvent> events = activityEventRepository.findAll();
        assertEquals(1, events.size(), "W MongoDB powinno pojawić się jedno zdarzenie po pomyślnym wypożyczeniu");
        assertEquals("BORROW", events.get(0).getEventType());
        assertEquals(readerId, events.get(0).getReaderId());
        assertEquals(publicationId, events.get(0).getPublicationId());
    }

    @Test
    void shouldRollbackSqlAndNotSyncToNoSqlDatabasesWhenBookIsNotAvailable() {
        BookCopy copy = bookCopyRepository.findById(copyId).orElseThrow();
        copy.setStatus(CopyStatus.BORROWED);
        bookCopyRepository.save(copy);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            libraryService.borrowBook(readerId, copyId);
        });
        assertEquals("Book copy is not available!", exception.getMessage());

        assertEquals(0, borrowingRepository.count());

        ReaderNode unchangedReaderNode = readerNodeRepository.findById(readerId).orElseThrow();
        assertTrue(unchangedReaderNode.getBorrowedPublications().isEmpty());

        assertEquals(0, activityEventRepository.count(), "Błąd w SQL musi powstrzymać zapis zdarzenia w MongoDB");
    }

    @Test
    void shouldFetchPublicationPageCrossingAllThreeDatabases() {
        PublicationPageDto dto = libraryService.getPublicationPage(publicationId, readerId);

        assertNotNull(dto);
        assertEquals("Spring Boot Microservices", dto.publicationDetails().getTitle());
        assertEquals(1, dto.availableCopiesCount());
        assertNotNull(dto.alsoBorrowed());

        List<ActivityEvent> events = activityEventRepository.findAll();
        assertEquals(1, events.size());
        assertEquals("VIEW", events.get(0).getEventType());
    }
}