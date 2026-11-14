package pk.academic.library.academiclibrary.mongo.documents;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Review {
    private String readerId;
    private int rating;
    private String content;
}