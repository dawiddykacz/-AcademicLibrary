package pk.academic.library.academiclibrary.sql;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pk.academic.library.academiclibrary.sql.entities.BookCopy;

@Repository
public interface BookCopyRepository extends JpaRepository<BookCopy, String> {}