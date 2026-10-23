package ru.mfa.lab3.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.mfa.lab3.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
