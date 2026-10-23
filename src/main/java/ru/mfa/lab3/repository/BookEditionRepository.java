package ru.mfa.lab3.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.mfa.lab3.entity.BookEdition;

public interface BookEditionRepository extends JpaRepository<BookEdition, Long> {
}
