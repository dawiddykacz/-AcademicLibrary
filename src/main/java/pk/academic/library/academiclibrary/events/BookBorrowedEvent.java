package pk.academic.library.academiclibrary.events;

public record BookBorrowedEvent(String readerId, String publicationId) {
}
