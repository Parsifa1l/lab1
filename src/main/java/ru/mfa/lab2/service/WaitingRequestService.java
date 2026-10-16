package ru.mfa.lab2.service;

import ru.mfa.lab2.dto.WaitingRequestDto;
import ru.mfa.lab2.exception.NotFoundException;
import ru.mfa.lab2.model.WaitingRequest;
import ru.mfa.lab2.model.WaitingStatus;
import ru.mfa.lab2.repository.BookEditionRepository;
import ru.mfa.lab2.repository.UserRepository;
import ru.mfa.lab2.repository.WaitingRequestRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class WaitingRequestService {

    private final WaitingRequestRepository waitingRequests;
    private final BookEditionRepository editions;
    private final UserRepository users;

    public WaitingRequestService(WaitingRequestRepository waitingRequests,
                                 BookEditionRepository editions, UserRepository users) {
        this.waitingRequests = waitingRequests;
        this.editions = editions;
        this.users = users;
    }

    public List<WaitingRequest> findAll() {
        return waitingRequests.findAll();
    }

    public WaitingRequest findById(Long id) {
        return waitingRequests.findById(id).orElseThrow(() -> new NotFoundException("Waiting request", id));
    }

    /** Дата постановки — время сервера, начальный статус — WAITING. */
    public WaitingRequest create(Long editionId, Long userId) {
        if (editions.findById(editionId).isEmpty()) {
            throw new NotFoundException("Book edition", editionId);
        }
        if (users.findById(userId).isEmpty()) {
            throw new NotFoundException("User", userId);
        }
        return waitingRequests.save(
                new WaitingRequest(null, editionId, userId, LocalDateTime.now(), WaitingStatus.WAITING));
    }

    public WaitingRequest update(Long id, WaitingRequestDto.Patch patch) {
        WaitingRequest current = findById(id);
        return waitingRequests.save(new WaitingRequest(id, current.editionId(), current.userId(),
                current.requestDate(), Objects.requireNonNullElse(patch.status(), current.status())));
    }

    public void delete(Long id) {
        findById(id);
        waitingRequests.deleteById(id);
    }
}
