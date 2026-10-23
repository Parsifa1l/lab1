package ru.mfa.lab3.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mfa.lab3.dto.WaitingRequestDto;
import ru.mfa.lab3.entity.BookEdition;
import ru.mfa.lab3.entity.User;
import ru.mfa.lab3.entity.WaitingRequest;
import ru.mfa.lab3.entity.WaitingStatus;
import ru.mfa.lab3.exception.NotFoundException;
import ru.mfa.lab3.repository.BookEditionRepository;
import ru.mfa.lab3.repository.UserRepository;
import ru.mfa.lab3.repository.WaitingRequestRepository;

import java.time.LocalDateTime;
import java.util.List;

/** Базовые CRUD-операции над заявками. Правила очереди - в LibraryOperationsService. */
@Service
@Transactional
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

    @Transactional(readOnly = true)
    public List<WaitingRequest> findAll() {
        return waitingRequests.findAll();
    }

    @Transactional(readOnly = true)
    public WaitingRequest findById(Long id) {
        return waitingRequests.findById(id).orElseThrow(() -> new NotFoundException("Waiting request", id));
    }

    public WaitingRequest create(WaitingRequestDto.Create request) {
        BookEdition edition = editions.findById(request.editionId())
                .orElseThrow(() -> new NotFoundException("Book edition", request.editionId()));
        User user = users.findById(request.userId())
                .orElseThrow(() -> new NotFoundException("User", request.userId()));
        return waitingRequests.saveAndFlush(
                new WaitingRequest(edition, user, LocalDateTime.now(), WaitingStatus.WAITING));
    }

    /** Корректность статуса READY (нужен закреплённый экземпляр) дополнительно защищает CHECK в БД. */
    public WaitingRequest update(Long id, WaitingRequestDto.Patch patch) {
        WaitingRequest request = findById(id);
        if (patch.status() != null) request.setStatus(patch.status());
        return waitingRequests.saveAndFlush(request);
    }

    public void delete(Long id) {
        waitingRequests.delete(findById(id));
        waitingRequests.flush();
    }
}
