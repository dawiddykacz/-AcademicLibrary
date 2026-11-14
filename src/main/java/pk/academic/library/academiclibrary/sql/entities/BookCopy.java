package pk.academic.library.academiclibrary.sql.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "book_copies")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookCopy {
    @Id
    private String id;

    @Column(nullable = false)
    private String publicationId;

    @Column(unique = true, nullable = false)
    private String inventoryNumber;

    @Enumerated(EnumType.STRING)
    private CopyStatus status;
}
