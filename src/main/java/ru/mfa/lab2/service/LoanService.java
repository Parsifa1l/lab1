package ru.mfa.lab2.service;

import ru.mfa.lab2.dto.LoanDto;
import ru.mfa.lab2.exception.ConflictException;
import ru.mfa.lab2.exception.NotFoundException;
import ru.mfa.lab2.model.Loan;
import ru.mfa.lab2.repository.CopyRepository;
import ru.mfa.lab2.repository.LoanRepository;
import ru.mfa.lab2.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class LoanService {

    private final LoanRepository loans;
    private final CopyRepository copies;
    private final UserRepository users;

    public LoanService(LoanRepository loans, CopyRepository copies, UserRepository users) {
        this.loans = loans;
        this.copies = copies;
        this.users = users;
    }

    public List<Loan> findAll() {
        return loans.findAll();
    }

    public Loan findById(Long id) {
        return loans.findById(id).orElseThrow(() -> new NotFoundException("Loan", id));
    }

    /** Дата выдачи — текущее время сервера. */
    public Loan create(Long copyId, Long userId) {
        checkCopyExists(copyId);
        checkUserExists(userId);
        checkCopyAvailable(copyId, null);
        return loans.save(new Loan(null, copyId, userId, LocalDateTime.now(), null));
    }

    public Loan update(Long id, LoanDto.Patch patch) {
        Loan current = findById(id);

        Long userId = current.userId();
        if (patch.userId() != null) {
            checkUserExists(patch.userId());
            userId = patch.userId();
        }

        LocalDateTime returnDate = current.returnDate();
        if (patch.returned() != null) {
            if (patch.returned()) {
                if (returnDate == null) {
                    returnDate = LocalDateTime.now(); // дата возврата — время сервера
                }
            } else if (returnDate != null) {
                checkCopyAvailable(current.copyId(), id); // повторное открытие выдачи
                returnDate = null;
            }
        }
        return loans.save(new Loan(id, current.copyId(), userId, current.loanDate(), returnDate));
    }

    public void delete(Long id) {
        findById(id);
        loans.deleteById(id);
    }

    private void checkCopyExists(Long copyId) {
        if (copies.findById(copyId).isEmpty()) {
            throw new NotFoundException("Copy", copyId);
        }
    }

    private void checkUserExists(Long userId) {
        if (users.findById(userId).isEmpty()) {
            throw new NotFoundException("User", userId);
        }
    }

    private void checkCopyAvailable(Long copyId, Long exceptLoanId) {
        boolean onLoan = loans.exists(l -> l.copyId().equals(copyId)
                && l.returnDate() == null
                && !l.id().equals(exceptLoanId));
        if (onLoan) {
            throw new ConflictException("Copy " + copyId + " is already on loan");
        }
    }
}
