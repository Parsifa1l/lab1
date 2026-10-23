package ru.mfa.lab3.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mfa.lab3.entity.BookEdition;
import ru.mfa.lab3.entity.Copy;
import ru.mfa.lab3.entity.CopyCondition;
import ru.mfa.lab3.entity.Loan;
import ru.mfa.lab3.entity.User;
import ru.mfa.lab3.entity.WaitingRequest;
import ru.mfa.lab3.entity.WaitingStatus;
import ru.mfa.lab3.exception.ConflictException;
import ru.mfa.lab3.exception.NotFoundException;
import ru.mfa.lab3.repository.BookEditionRepository;
import ru.mfa.lab3.repository.CopyRepository;
import ru.mfa.lab3.repository.LoanRepository;
import ru.mfa.lab3.repository.UserRepository;
import ru.mfa.lab3.repository.WaitingRequestRepository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Бизнес-операции библиотечного абонемента. Каждая операция выполняется в одной транзакции:
 * либо все изменения применяются, либо (при нарушении правила) не применяется ни одно.
 */
@Service
@Transactional
public class LibraryOperationsService {

    public static final int LOAN_DAYS = 14;
    public static final int MAX_ACTIVE_LOANS = 3;
    public static final int MAX_RENEWALS = 2;

    private static final List<WaitingStatus> ACTIVE_REQUEST_STATUSES =
            List.of(WaitingStatus.WAITING, WaitingStatus.READY);

    private final LoanRepository loans;
    private final CopyRepository copies;
    private final UserRepository users;
    private final BookEditionRepository editions;
    private final WaitingRequestRepository waitingRequests;

    public LibraryOperationsService(LoanRepository loans, CopyRepository copies, UserRepository users,
                                    BookEditionRepository editions, WaitingRequestRepository waitingRequests) {
        this.loans = loans;
        this.copies = copies;
        this.users = users;
        this.editions = editions;
        this.waitingRequests = waitingRequests;
    }

    public record ReturnResult(Loan loan, boolean overdue, WaitingRequest reservation) {}

    /**
     * Операция 1. Выдача экземпляра читателю (затрагивает: экземпляр, читателя, выдачу, заявку).
     * Нельзя выдать: повреждённый экземпляр; экземпляр, который уже на руках;
     * экземпляр, закреплённый за другим читателем; читателю с просрочкой, с 3 книгами на руках
     * или уже держащему другой экземпляр этого издания.
     */
    public Loan checkout(Long copyId, Long userId) {
        Copy copy = copies.findByIdForUpdate(copyId).orElseThrow(() -> new NotFoundException("Copy", copyId));
        User user = users.findById(userId).orElseThrow(() -> new NotFoundException("User", userId));
        LocalDateTime now = LocalDateTime.now();

        if (copy.getCondition() == CopyCondition.DAMAGED) {
            throw new ConflictException("Damaged copy cannot be lent");
        }
        if (loans.countActiveByCopy(copyId) > 0) {
            throw new ConflictException("Copy " + copyId + " is already on loan");
        }
        WaitingRequest reservation = waitingRequests
                .findFirstByReservedCopy_IdAndStatus(copyId, WaitingStatus.READY).orElse(null);
        if (reservation != null && !reservation.getUser().getId().equals(userId)) {
            throw new ConflictException("Copy " + copyId + " is reserved for another reader");
        }
        if (loans.countOverdueByUser(userId, now) > 0) {
            throw new ConflictException("Reader has overdue loans and cannot borrow new books");
        }
        if (loans.countActiveByUser(userId) >= MAX_ACTIVE_LOANS) {
            throw new ConflictException("Reader already has " + MAX_ACTIVE_LOANS + " books on loan");
        }
        if (loans.countActiveByUserAndEdition(userId, copy.getEdition().getId()) > 0) {
            throw new ConflictException("Reader already holds a copy of this edition");
        }

        Loan loan = loans.saveAndFlush(new Loan(copy, user, now, now.plusDays(LOAN_DAYS)));
        if (reservation != null) {
            reservation.setStatus(WaitingStatus.FULFILLED);
            reservation.setReservedCopy(null);
            waitingRequests.saveAndFlush(reservation);
        }
        return loan;
    }

    /**
     * Операция 2. Возврат книги (затрагивает: выдачу, экземпляр, очередь).
     * Если книгу вернули повреждённой, она больше не выдаётся. Иначе экземпляр закрепляется за
     * первым в очереди читателем (заявка WAITING -> READY) или просто становится свободным.
     */
    public ReturnResult returnLoan(Long loanId, CopyCondition returnedCondition) {
        Loan loan = loans.findById(loanId).orElseThrow(() -> new NotFoundException("Loan", loanId));
        if (loan.getReturnDate() != null) {
            throw new ConflictException("Loan " + loanId + " is already returned");
        }
        Copy copy = copies.findByIdForUpdate(loan.getCopy().getId())
                .orElseThrow(() -> new NotFoundException("Copy", loan.getCopy().getId()));
        LocalDateTime now = LocalDateTime.now();

        loan.setReturnDate(now);
        loans.saveAndFlush(loan);
        if (returnedCondition != null) {
            copy.setCondition(returnedCondition);
            copies.saveAndFlush(copy);
        }

        WaitingRequest reservation = null;
        if (copy.getCondition() != CopyCondition.DAMAGED) {
            reservation = assignToNextInQueue(copy);
        }
        return new ReturnResult(loan, now.isAfter(loan.getDueDate()), reservation);
    }

    /**
     * Операция 3. Продление выдачи на 14 дней (затрагивает: выдачу, очередь на издание).
     * Нельзя: если выдача уже возвращена или просрочена, продлена 2 раза,
     * либо на это издание есть очередь.
     */
    public Loan renewLoan(Long loanId) {
        Loan loan = loans.findById(loanId).orElseThrow(() -> new NotFoundException("Loan", loanId));
        LocalDateTime now = LocalDateTime.now();

        if (loan.getReturnDate() != null) {
            throw new ConflictException("Loan " + loanId + " is already returned");
        }
        if (now.isAfter(loan.getDueDate())) {
            throw new ConflictException("Overdue loan cannot be renewed");
        }
        if (loan.getRenewals() >= MAX_RENEWALS) {
            throw new ConflictException("Loan can be renewed at most " + MAX_RENEWALS + " times");
        }
        Long editionId = loan.getCopy().getEdition().getId();
        if (waitingRequests.countByEdition_IdAndStatus(editionId, WaitingStatus.WAITING) > 0) {
            throw new ConflictException("Other readers are waiting for this edition");
        }

        loan.setDueDate(loan.getDueDate().plusDays(LOAN_DAYS));
        loan.setRenewals(loan.getRenewals() + 1);
        return loans.saveAndFlush(loan);
    }

    /**
     * Операция 4. Постановка в очередь на издание (затрагивает: издание, экземпляры, выдачи, читателя).
     * Нельзя: если есть свободный экземпляр (нужно просто взять его), если у читателя это издание
     * уже на руках, если читатель уже стоит в очереди на него или у издания нет экземпляров.
     */
    public WaitingRequest joinWaitingList(Long editionId, Long userId) {
        BookEdition edition = editions.findById(editionId)
                .orElseThrow(() -> new NotFoundException("Book edition", editionId));
        User user = users.findById(userId).orElseThrow(() -> new NotFoundException("User", userId));

        if (copies.countByEdition_Id(editionId) == 0) {
            throw new ConflictException("Edition has no copies in the library");
        }
        if (copies.countAvailable(editionId, CopyCondition.DAMAGED, WaitingStatus.READY) > 0) {
            throw new ConflictException("Free copies are available - borrow one instead of waiting");
        }
        if (loans.countActiveByUserAndEdition(userId, editionId) > 0) {
            throw new ConflictException("Reader already holds a copy of this edition");
        }
        if (waitingRequests.countActiveByEditionAndUser(editionId, userId, ACTIVE_REQUEST_STATUSES) > 0) {
            throw new ConflictException("Reader is already in the queue for this edition");
        }
        return waitingRequests.saveAndFlush(
                new WaitingRequest(edition, user, LocalDateTime.now(), WaitingStatus.WAITING));
    }

    /**
     * Операция 5. Отмена заявки (затрагивает: заявку, закреплённый экземпляр, очередь).
     * Заявку можно отменить только пока она WAITING или READY. Если у отменяемой заявки был
     * закреплён экземпляр, он передаётся следующему в очереди либо освобождается.
     */
    public WaitingRequest cancelWaitingRequest(Long requestId) {
        WaitingRequest request = waitingRequests.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Waiting request", requestId));
        if (!ACTIVE_REQUEST_STATUSES.contains(request.getStatus())) {
            throw new ConflictException("Request is already " + request.getStatus() + " and cannot be cancelled");
        }

        Copy reserved = request.getReservedCopy();
        boolean wasReady = request.getStatus() == WaitingStatus.READY;
        request.setStatus(WaitingStatus.CANCELLED);
        request.setReservedCopy(null);
        waitingRequests.saveAndFlush(request);

        if (wasReady && reserved != null) {
            Copy copy = copies.findByIdForUpdate(reserved.getId())
                    .orElseThrow(() -> new NotFoundException("Copy", reserved.getId()));
            if (copy.getCondition() != CopyCondition.DAMAGED) {
                assignToNextInQueue(copy);
            }
        }
        return request;
    }

    /** Закрепляет экземпляр за самым ранним читателем из очереди. Возвращает заявку или null, если очереди нет. */
    private WaitingRequest assignToNextInQueue(Copy copy) {
        return waitingRequests
                .findFirstByEdition_IdAndStatusOrderByRequestDateAscIdAsc(copy.getEdition().getId(), WaitingStatus.WAITING)
                .map(next -> {
                    next.setStatus(WaitingStatus.READY);
                    next.setReservedCopy(copy);
                    return waitingRequests.saveAndFlush(next);
                })
                .orElse(null);
    }
}
