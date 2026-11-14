package pk.academic.library.academiclibrary.events;

import java.time.LocalDate;

public record BookBorrowedEvent(String readerId,
                                String publicationId,
                                LocalDate borrowDate,
                                int days) {
}
