package pk.academic.library.academiclibrary.sql.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "readers")
@Data @NoArgsConstructor @AllArgsConstructor
public class Reader {
    @Id
    private String id;
    private String firstName;
    private String lastName;
    private String email;
}