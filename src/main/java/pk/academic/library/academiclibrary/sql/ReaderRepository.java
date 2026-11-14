package pk.academic.library.academiclibrary.sql;

import org.springframework.data.jpa.repository.JpaRepository;
import pk.academic.library.academiclibrary.sql.entities.Reader;

public interface ReaderRepository extends JpaRepository<Reader, String> {
}
