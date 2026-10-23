package ru.mfa.lab3.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mfa.lab3.dto.UserDto;
import ru.mfa.lab3.entity.User;
import ru.mfa.lab3.exception.NotFoundException;
import ru.mfa.lab3.repository.UserRepository;

import java.util.List;

@Service
@Transactional
public class UserService {

    private final UserRepository users;

    public UserService(UserRepository users) {
        this.users = users;
    }

    @Transactional(readOnly = true)
    public List<User> findAll() {
        return users.findAll();
    }

    @Transactional(readOnly = true)
    public User findById(Long id) {
        return users.findById(id).orElseThrow(() -> new NotFoundException("User", id));
    }

    public User create(UserDto.Create request) {
        return users.saveAndFlush(new User(request.name(), request.email()));
    }

    public User update(Long id, UserDto.Patch patch) {
        User user = findById(id);
        if (patch.name() != null) user.setName(patch.name());
        if (patch.email() != null) user.setEmail(patch.email());
        return users.saveAndFlush(user);
    }

    /** Выдачи и заявки пользователя удаляет сама БД (ON DELETE CASCADE). */
    public void delete(Long id) {
        users.delete(findById(id));
        users.flush();
    }
}
