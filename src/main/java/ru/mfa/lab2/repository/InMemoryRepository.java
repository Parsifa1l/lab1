package ru.mfa.lab2.repository;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Predicate;

/** Простое хранилище в памяти (коллекция) с автоинкрементом id. */
public abstract class InMemoryRepository<T> {

    private final Map<Long, T> store = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong();

    protected abstract Long idOf(T entity);

    protected abstract T withId(T entity, Long id);

    public List<T> findAll() {
        return store.values().stream().sorted(Comparator.comparing(this::idOf)).toList();
    }

    public List<T> findAll(Predicate<T> filter) {
        return store.values().stream().filter(filter).sorted(Comparator.comparing(this::idOf)).toList();
    }

    public Optional<T> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    public T save(T entity) {
        T toSave = idOf(entity) == null ? withId(entity, sequence.incrementAndGet()) : entity;
        store.put(idOf(toSave), toSave);
        return toSave;
    }

    public boolean exists(Predicate<T> filter) {
        return store.values().stream().anyMatch(filter);
    }

    public boolean deleteById(Long id) {
        return store.remove(id) != null;
    }

    public void deleteAll(Predicate<T> filter) {
        store.values().removeIf(filter);
    }
}
