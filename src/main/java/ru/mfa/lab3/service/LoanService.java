package ru.mfa.lab3.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mfa.lab3.dto.LoanDto;
import ru.mfa.lab3.entity.Copy;
import ru.mfa.lab3.entity.Loan;
import ru.mfa.lab3.entity.User;
import ru.mfa.lab3.exception.NotFoundException;
import ru.mfa.lab3.repository.CopyRepository;
import ru.mfa.lab3.repository.LoanRepository;
import ru.mfa.lab3.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

/** Базовые CRUD-операции над выдачами. Правила выдачи/возврата - в LibraryOperationsService. */
@Service
@Transactional
public class LoanService {

    private final LoanRepository loans;
    private final CopyRepository copies;
    private final UserRepository users;

    public LoanService(LoanRepository loans, CopyRepository copies, UserRepository users) {
        this.loans = loans;
        this.copies = copies;
        this.users = users;
    }

    @Transactional(readOnly = true)
    public List<Loan> findAll() {
        return loans.findAll();
    }

    @Transactional(readOnly = true)
    public Loan findById(Long id) {
        return loans.findById(id).orElseThrow(() -> new NotFoundException("Loan", id));
    }

    public Loan create(LoanDto.Create request) {
        Copy copy = copies.findById(request.copyId())
                .orElseThrow(() -> new NotFoundException("Copy", request.copyId()));
        User user = findUser(request.userId());
        LocalDateTime now = LocalDateTime.now();
        return loans.saveAndFlush(new Loan(copy, user, now, now.plusDays(LibraryOperationsService.LOAN_DAYS)));
    }

    public Loan update(Long id, LoanDto.Patch patch) {
        Loan loan = findById(id);
        if (patch.userId() != null) loan.setUser(findUser(patch.userId()));
        if (patch.dueDate() != null) loan.setDueDate(patch.dueDate());
        return loans.saveAndFlush(loan);
    }

    public void delete(Long id) {
        loans.delete(findById(id));
        loans.flush();
    }

    private User findUser(Long userId) {
        return users.findById(userId).orElseThrow(() -> new NotFoundException("User", userId));
    }
}
