package ru.mfa.lab3.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.mfa.lab3.entity.Copy;
import ru.mfa.lab3.entity.CopyCondition;
import ru.mfa.lab3.entity.WaitingStatus;

import java.util.Optional;

public interface CopyRepository extends JpaRepository<Copy, Long> {

    /** Блокирует строку экземпляра до конца транзакции, чтобы две операции не выдали его одновременно. */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from Copy c where c.id = :id")
    Optional<Copy> findByIdForUpdate(@Param("id") Long id);

    long countByEdition_Id(Long editionId);

    /** Экземпляры издания, которые сейчас можно выдать: не повреждены, не на руках, не закреплены за очередью. */
    @Query("""
            select count(c) from Copy c
            where c.edition.id = :editionId
              and c.condition <> :damaged
              and not exists (select l.id from Loan l where l.copy = c and l.returnDate is null)
              and not exists (select w.id from WaitingRequest w where w.reservedCopy = c and w.status = :ready)
            """)
    long countAvailable(@Param("editionId") Long editionId,
                        @Param("damaged") CopyCondition damaged,
                        @Param("ready") WaitingStatus ready);
}
