package ru.mfa.lab2.service;

import ru.mfa.lab2.dto.UserDto;
import ru.mfa.lab2.exception.NotFoundException;
import ru.mfa.lab2.model.User;
import ru.mfa.lab2.repository.LoanRepository;
import ru.mfa.lab2.repository.UserRepository;
import ru.mfa.lab2.repository.WaitingRequestRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class UserService {

    private final UserRepository users;
    private final LoanRepository loans;
    private final WaitingRequestRepository waitingRequests;

    public UserService(UserRepository users, LoanRepository loans, WaitingRequestRepository waitingRequests) {
        this.users = users;
        this.loans = loans;
        this.waitingRequests = waitingRequests;
    }

    public List<User> findAll() {
        return users.findAll();
    }

    public User findById(Long id) {
        return users.findById(id).orElseThrow(() -> new NotFoundException("User", id));
    }

    public User create(User user) {
        return users.save(user);
    }

    public User update(Long id, UserDto.Patch patch) {
        User current = findById(id);
        return users.save(new User(id,
                Objects.requireNonNullElse(patch.name(), current.name()),
                Objects.requireNonNullElse(patch.email(), current.email())));
    }

    /** Удаляет пользователя вместе с его выдачами и заявками на ожидание. */
    public void delete(Long id) {
        findById(id);
        loans.deleteAll(l -> l.userId().equals(id));
        waitingRequests.deleteAll(w -> w.userId().equals(id));
        users.deleteById(id);
    }
}
