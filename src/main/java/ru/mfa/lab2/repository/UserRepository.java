package ru.mfa.lab2.repository;

import ru.mfa.lab2.model.User;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepository extends InMemoryRepository<User> {

    @Override
    protected Long idOf(User e) {
        return e.id();
    }

    @Override
    protected User withId(User e, Long id) {
        return new User(id, e.name(), e.email());
    }
}
