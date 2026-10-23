package ru.mfa.lab3.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.mfa.lab3.entity.WaitingRequest;
import ru.mfa.lab3.entity.WaitingStatus;

import java.util.Collection;
import java.util.Optional;

public interface WaitingRequestRepository extends JpaRepository<WaitingRequest, Long> {

    /** Самая ранняя заявка в очереди на издание (FIFO). */
    Optional<WaitingRequest> findFirstByEdition_IdAndStatusOrderByRequestDateAscIdAsc(Long editionId, WaitingStatus status);

    Optional<WaitingRequest> findFirstByReservedCopy_IdAndStatus(Long copyId, WaitingStatus status);

    long countByEdition_IdAndStatus(Long editionId, WaitingStatus status);

    @Query("select count(w) from WaitingRequest w where w.edition.id = :editionId and w.user.id = :userId and w.status in :statuses")
    long countActiveByEditionAndUser(@Param("editionId") Long editionId,
                                     @Param("userId") Long userId,
                                     @Param("statuses") Collection<WaitingStatus> statuses);
}
