package pk.academic.library.academiclibrary.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import pk.academic.library.academiclibrary.BaseIntegrationTest;
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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class LibraryServiceIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private LibraryService libraryService;

    @Autowired
    private ReaderRepository readerRepository;

    @Autowired
    private BookCopyRepository bookCopyRepository;

    @Autowired
    private BorrowingRepository borrowingRepository;

    @Autowired
    private ReaderNodeRepository readerNodeRepository;

    @Autowired
    private PublicationNodeRepository publicationNodeRepository;

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
    }

    @Test
    void shouldSuccessfullyBorrowBookAndSyncAcrossDatabases() {
        assertEquals(0, borrowingRepository.count(), "Baza SQL powinna być pusta przed wypożyczeniem");

        libraryService.borrowBook(readerId, copyId);

        BookCopy updatedCopy = bookCopyRepository.findById(copyId).orElseThrow();
        assertEquals(CopyStatus.BORROWED, updatedCopy.getStatus(), "Status egzemplarza w SQL musi zmienić się na BORROWED");

        List<Borrowing> borrowings = borrowingRepository.findAll();
        assertEquals(1, borrowings.size(), "Musi zostać utworzony dokładnie jeden rekord wypożyczenia w SQL");
        Borrowing borrowing = borrowings.get(0);
        assertEquals(readerId, borrowing.getReader().getId(), "Wypożyczenie w SQL musi wskazywać na poprawnego czytelnika");
        assertEquals(copyId, borrowing.getBookCopy().getId(), "Wypożyczenie w SQL musi wskazywać na poprawny egzemplarz");

        ReaderNode updatedReaderNode = readerNodeRepository.findById(readerId).orElseThrow();
        assertNotNull(updatedReaderNode.getBorrowedPublications(), "Zbiór wypożyczeń w Neo4j nie może być null");
        assertEquals(1, updatedReaderNode.getBorrowedPublications().size(), "W Neo4j musi pojawić się jedna relacja wypożyczenia");

        PublicationNode linkedPublication = updatedReaderNode.getBorrowedPublications().iterator().next();
        assertEquals(publicationId, linkedPublication.getId(), "Relacja w Neo4j musi wskazywać na poprawne ID publikacji");
    }

    @Test
    void shouldRollbackSqlAndNotSyncToNeo4jWhenBookIsNotAvailable() {
        BookCopy copy = bookCopyRepository.findById(copyId).orElseThrow();
        copy.setStatus(CopyStatus.BORROWED);
        bookCopyRepository.save(copy);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            libraryService.borrowBook(readerId, copyId);
        });
        assertEquals("Book copy is not available!", exception.getMessage());

        assertEquals(0, borrowingRepository.count());

        ReaderNode unchangedReaderNode = readerNodeRepository.findById(readerId).orElseThrow();
        assertTrue(unchangedReaderNode.getBorrowedPublications().isEmpty(), "Neo4j nie powinno zawierać relacji przy wycofanej transakcji w SQL");
    }
}