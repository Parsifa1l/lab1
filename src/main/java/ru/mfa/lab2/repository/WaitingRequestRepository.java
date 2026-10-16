package ru.mfa.lab2.repository;

import ru.mfa.lab2.model.WaitingRequest;
import org.springframework.stereotype.Repository;

@Repository
public class WaitingRequestRepository extends InMemoryRepository<WaitingRequest> {

    @Override
    protected Long idOf(WaitingRequest e) {
        return e.id();
    }

    @Override
    protected WaitingRequest withId(WaitingRequest e, Long id) {
        return new WaitingRequest(id, e.editionId(), e.userId(), e.requestDate(), e.status());
    }
}
