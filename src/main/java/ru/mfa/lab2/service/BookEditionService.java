package ru.mfa.lab2.service;

import ru.mfa.lab2.dto.BookEditionDto;
import ru.mfa.lab2.exception.ConflictException;
import ru.mfa.lab2.exception.NotFoundException;
import ru.mfa.lab2.model.BookEdition;
import ru.mfa.lab2.repository.BookEditionRepository;
import ru.mfa.lab2.repository.CopyRepository;
import ru.mfa.lab2.repository.LoanRepository;
import ru.mfa.lab2.repository.WaitingRequestRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class BookEditionService {

    private final BookEditionRepository editions;
    private final CopyRepository copies;
    private final LoanRepository loans;
    private final WaitingRequestRepository waitingRequests;

    public BookEditionService(BookEditionRepository editions, CopyRepository copies,
                              LoanRepository loans, WaitingRequestRepository waitingRequests) {
        this.editions = editions;
        this.copies = copies;
        this.loans = loans;
        this.waitingRequests = waitingRequests;
    }

    public List<BookEdition> findAll() {
        return editions.findAll();
    }

    public BookEdition findById(Long id) {
        return editions.findById(id).orElseThrow(() -> new NotFoundException("Book edition", id));
    }

    public BookEdition create(BookEdition edition) {
        checkIsbnUnique(edition.isbn(), null);
        return editions.save(edition);
    }

    public BookEdition update(Long id, BookEditionDto.Patch patch) {
        BookEdition current = findById(id);
        String isbn = Objects.requireNonNullElse(patch.isbn(), current.isbn());
        checkIsbnUnique(isbn, id);
        return editions.save(new BookEdition(
                id,
                Objects.requireNonNullElse(patch.title(), current.title()),
                Objects.requireNonNullElse(patch.author(), current.author()),
                isbn));
    }

    /** Каскадно удаляет экземпляры, их выдачи и заявки на ожидание этого издания. */
    public void delete(Long id) {
        findById(id);
        copies.findAll(c -> c.editionId().equals(id)).forEach(c -> {
            loans.deleteAll(l -> l.copyId().equals(c.id()));
            copies.deleteById(c.id());
        });
        waitingRequests.deleteAll(w -> w.editionId().equals(id));
        editions.deleteById(id);
    }

    private void checkIsbnUnique(String isbn, Long selfId) {
        boolean taken = editions.exists(e -> e.isbn().equals(isbn) && !e.id().equals(selfId));
        if (taken) {
            throw new ConflictException("Book edition with ISBN " + isbn + " already exists");
        }
    }
}
