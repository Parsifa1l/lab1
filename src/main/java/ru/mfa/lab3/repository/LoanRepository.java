package ru.mfa.lab3.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.mfa.lab3.entity.Loan;

import java.time.LocalDateTime;

public interface LoanRepository extends JpaRepository<Loan, Long> {

    @Query("select count(l) from Loan l where l.copy.id = :copyId and l.returnDate is null")
    long countActiveByCopy(@Param("copyId") Long copyId);

    @Query("select count(l) from Loan l where l.user.id = :userId and l.returnDate is null")
    long countActiveByUser(@Param("userId") Long userId);

    @Query("select count(l) from Loan l where l.user.id = :userId and l.returnDate is null and l.dueDate < :now")
    long countOverdueByUser(@Param("userId") Long userId, @Param("now") LocalDateTime now);

    @Query("select count(l) from Loan l where l.user.id = :userId and l.copy.edition.id = :editionId and l.returnDate is null")
    long countActiveByUserAndEdition(@Param("userId") Long userId, @Param("editionId") Long editionId);
}
