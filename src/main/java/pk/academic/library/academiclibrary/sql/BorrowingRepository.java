package pk.academic.library.academiclibrary.sql;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pk.academic.library.academiclibrary.sql.entities.Borrowing;

import java.util.List;

@Repository
public interface BorrowingRepository extends JpaRepository<Borrowing, Long> {
    List<Borrowing> findByReaderId(String readerId);
}
