package ru.mfa.lab2.repository;

import ru.mfa.lab2.model.Copy;
import org.springframework.stereotype.Repository;

@Repository
public class CopyRepository extends InMemoryRepository<Copy> {

    @Override
    protected Long idOf(Copy e) {
        return e.id();
    }

    @Override
    protected Copy withId(Copy e, Long id) {
        return new Copy(id, e.editionId(), e.inventoryNumber(), e.condition());
    }
}
