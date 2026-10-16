package ru.mfa.lab2.repository;

import ru.mfa.lab2.model.Loan;
import org.springframework.stereotype.Repository;

@Repository
public class LoanRepository extends InMemoryRepository<Loan> {

    @Override
    protected Long idOf(Loan e) {
        return e.id();
    }

    @Override
    protected Loan withId(Loan e, Long id) {
        return new Loan(id, e.copyId(), e.userId(), e.loanDate(), e.returnDate());
    }
}
