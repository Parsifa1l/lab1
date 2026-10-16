package ru.mfa.lab2.repository;

import ru.mfa.lab2.model.BookEdition;
import org.springframework.stereotype.Repository;

@Repository
public class BookEditionRepository extends InMemoryRepository<BookEdition> {

    @Override
    protected Long idOf(BookEdition e) {
        return e.id();
    }

    @Override
    protected BookEdition withId(BookEdition e, Long id) {
        return new BookEdition(id, e.title(), e.author(), e.isbn());
    }
}
