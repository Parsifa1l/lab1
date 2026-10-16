package ru.mfa.lab2.service;

import ru.mfa.lab2.dto.CopyDto;
import ru.mfa.lab2.exception.ConflictException;
import ru.mfa.lab2.exception.NotFoundException;
import ru.mfa.lab2.model.Copy;
import ru.mfa.lab2.repository.BookEditionRepository;
import ru.mfa.lab2.repository.CopyRepository;
import ru.mfa.lab2.repository.LoanRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class CopyService {

    private final CopyRepository copies;
    private final BookEditionRepository editions;
    private final LoanRepository loans;

    public CopyService(CopyRepository copies, BookEditionRepository editions, LoanRepository loans) {
        this.copies = copies;
        this.editions = editions;
        this.loans = loans;
    }

    public List<Copy> findAll() {
        return copies.findAll();
    }

    public Copy findById(Long id) {
        return copies.findById(id).orElseThrow(() -> new NotFoundException("Copy", id));
    }

    public Copy create(Copy copy) {
        checkEditionExists(copy.editionId());
        checkInventoryNumberUnique(copy.inventoryNumber(), null);
        return copies.save(copy);
    }

    public Copy update(Long id, CopyDto.Patch patch) {
        Copy current = findById(id);
        Long editionId = Objects.requireNonNullElse(patch.editionId(), current.editionId());
        String inventoryNumber = Objects.requireNonNullElse(patch.inventoryNumber(), current.inventoryNumber());
        checkEditionExists(editionId);
        checkInventoryNumberUnique(inventoryNumber, id);
        return copies.save(new Copy(id, editionId, inventoryNumber,
                Objects.requireNonNullElse(patch.condition(), current.condition())));
    }

    /** Удаляет экземпляр вместе с его выдачами, чтобы не оставалось висячих ссылок. */
    public void delete(Long id) {
        findById(id);
        loans.deleteAll(l -> l.copyId().equals(id));
        copies.deleteById(id);
    }

    private void checkEditionExists(Long editionId) {
        if (editions.findById(editionId).isEmpty()) {
            throw new NotFoundException("Book edition", editionId);
        }
    }

    private void checkInventoryNumberUnique(String number, Long selfId) {
        boolean taken = copies.exists(c -> c.inventoryNumber().equals(number) && !c.id().equals(selfId));
        if (taken) {
            throw new ConflictException("Copy with inventory number " + number + " already exists");
        }
    }
}
