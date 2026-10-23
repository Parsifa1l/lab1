package ru.mfa.lab3.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mfa.lab3.dto.CopyDto;
import ru.mfa.lab3.entity.BookEdition;
import ru.mfa.lab3.entity.Copy;
import ru.mfa.lab3.exception.NotFoundException;
import ru.mfa.lab3.repository.BookEditionRepository;
import ru.mfa.lab3.repository.CopyRepository;

import java.util.List;

@Service
@Transactional
public class CopyService {

    private final CopyRepository copies;
    private final BookEditionRepository editions;

    public CopyService(CopyRepository copies, BookEditionRepository editions) {
        this.copies = copies;
        this.editions = editions;
    }

    @Transactional(readOnly = true)
    public List<Copy> findAll() {
        return copies.findAll();
    }

    @Transactional(readOnly = true)
    public Copy findById(Long id) {
        return copies.findById(id).orElseThrow(() -> new NotFoundException("Copy", id));
    }

    public Copy create(CopyDto.Create request) {
        BookEdition edition = findEdition(request.editionId());
        return copies.saveAndFlush(new Copy(edition, request.inventoryNumber(), request.condition()));
    }

    public Copy update(Long id, CopyDto.Patch patch) {
        Copy copy = findById(id);
        if (patch.editionId() != null) copy.setEdition(findEdition(patch.editionId()));
        if (patch.inventoryNumber() != null) copy.setInventoryNumber(patch.inventoryNumber());
        if (patch.condition() != null) copy.setCondition(patch.condition());
        return copies.saveAndFlush(copy);
    }

    /** Выдачи экземпляра удаляет сама БД (ON DELETE CASCADE). */
    public void delete(Long id) {
        copies.delete(findById(id));
        copies.flush();
    }

    private BookEdition findEdition(Long editionId) {
        return editions.findById(editionId).orElseThrow(() -> new NotFoundException("Book edition", editionId));
    }
}
