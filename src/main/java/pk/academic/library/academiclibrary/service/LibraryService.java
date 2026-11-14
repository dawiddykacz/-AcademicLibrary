package pk.academic.library.academiclibrary.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import pk.academic.library.academiclibrary.events.BookBorrowedEvent;

import pk.academic.library.academiclibrary.sql.BookCopyRepository;
import pk.academic.library.academiclibrary.sql.BorrowingRepository;
import pk.academic.library.academiclibrary.sql.ReaderRepository;
import pk.academic.library.academiclibrary.sql.entities.BookCopy;
import pk.academic.library.academiclibrary.sql.entities.Borrowing;
import pk.academic.library.academiclibrary.sql.entities.CopyStatus;
import pk.academic.library.academiclibrary.sql.entities.BorrowingStatus;
import pk.academic.library.academiclibrary.sql.entities.Reader;

import pk.academic.library.academiclibrary.neo4j.ReaderNodeRepository;
import pk.academic.library.academiclibrary.neo4j.PublicationNodeRepository;
import pk.academic.library.academiclibrary.neo4j.node.PublicationNode;
import pk.academic.library.academiclibrary.neo4j.node.ReaderNode;

import java.time.LocalDate;
import java.util.HashSet;

@Service
@RequiredArgsConstructor
public class LibraryService {
    private final BorrowingRepository borrowingRepository;
    private final BookCopyRepository bookCopyRepository;
    private final ReaderRepository readerRepository;

    private final ReaderNodeRepository readerNodeRepository;
    private final PublicationNodeRepository publicationNodeRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public void borrowBook(String readerId, String copyId) {
        Reader reader = readerRepository.findById(readerId)
                .orElseThrow(() -> new RuntimeException("Reader not found in SQL"));

        BookCopy copy = bookCopyRepository.findById(copyId)
                .orElseThrow(() -> new RuntimeException("Copy not found in SQL"));

        if (copy.getStatus() != CopyStatus.AVAILABLE) {
            throw new RuntimeException("Book copy is not available!");
        }

        copy.setStatus(CopyStatus.BORROWED);
        bookCopyRepository.save(copy);

        Borrowing borrowing = new Borrowing(null, reader, copy, LocalDate.now(), LocalDate.now().plusDays(14), null, BorrowingStatus.ACTIVE);
        borrowingRepository.save(borrowing);

        eventPublisher.publishEvent(new BookBorrowedEvent(readerId, copy.getPublicationId()));
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void syncBorrowingToNeo4j(BookBorrowedEvent event) {
        ReaderNode readerNode = readerNodeRepository.findById(event.readerId()).orElseThrow();
        PublicationNode pubNode = publicationNodeRepository.findById(event.publicationId()).orElseThrow();

        if (readerNode.getBorrowedPublications() == null) {
            readerNode.setBorrowedPublications(new HashSet<>());
        }

        readerNode.getBorrowedPublications().add(pubNode);
        readerNodeRepository.save(readerNode);
    }
}