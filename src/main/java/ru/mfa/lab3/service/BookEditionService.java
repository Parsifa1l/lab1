package ru.mfa.lab3.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mfa.lab3.dto.BookEditionDto;
import ru.mfa.lab3.entity.BookEdition;
import ru.mfa.lab3.exception.NotFoundException;
import ru.mfa.lab3.repository.BookEditionRepository;

import java.util.List;

@Service
@Transactional
public class BookEditionService {

    private final BookEditionRepository editions;

    public BookEditionService(BookEditionRepository editions) {
        this.editions = editions;
    }

    @Transactional(readOnly = true)
    public List<BookEdition> findAll() {
        return editions.findAll();
    }

    @Transactional(readOnly = true)
    public BookEdition findById(Long id) {
        return editions.findById(id).orElseThrow(() -> new NotFoundException("Book edition", id));
    }

    public BookEdition create(BookEditionDto.Create request) {
        return editions.saveAndFlush(new BookEdition(request.title(), request.author(), request.isbn()));
    }

    public BookEdition update(Long id, BookEditionDto.Patch patch) {
        BookEdition edition = findById(id);
        if (patch.title() != null) edition.setTitle(patch.title());
        if (patch.author() != null) edition.setAuthor(patch.author());
        if (patch.isbn() != null) edition.setIsbn(patch.isbn());
        return editions.saveAndFlush(edition);
    }

    /** Экземпляры, их выдачи и заявки на издание удаляет сама БД (ON DELETE CASCADE). */
    public void delete(Long id) {
        editions.delete(findById(id));
        editions.flush();
    }
}
