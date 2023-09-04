package ru.job4j.dreamjob.repository;

import ru.job4j.dreamjob.model.Vacancy;

import java.time.LocalDateTime;
import java.util.*;

public class MemoryVacancyRepository implements VacancyRepository {

    private static final MemoryVacancyRepository INSTANCE = new MemoryVacancyRepository();

    private int nextId = 1;

    private final Map<Integer, Vacancy> vacancies = new HashMap<>();

    private MemoryVacancyRepository() {
        save(new Vacancy(0, "Intern Java Developer", "Good desc", LocalDateTime.of(2023, 9, 3, 10, 10)));
        save(new Vacancy(0, "Junior Java Developer", "Same", LocalDateTime.of(2023, 9, 3, 10, 11)));
        save(new Vacancy(0, "Junior+ Java Developer", "Same 2", LocalDateTime.of(2023, 9, 3, 10, 12)));
        save(new Vacancy(0, "Middle Java Developer", "Bad", LocalDateTime.of(2023, 9, 3, 10, 13)));
        save(new Vacancy(0, "Middle+ Java Developer", "Very bad", LocalDateTime.of(2023, 9, 3, 10, 14)));
        save(new Vacancy(0, "Senior Java Developer", "Brilliant", LocalDateTime.of(2023, 9, 3, 10, 15)));
    }

    public static MemoryVacancyRepository getInstance() {
        return INSTANCE;
    }

    @Override
    public Vacancy save(Vacancy vacancy) {
        vacancy.setId(nextId++);
        vacancies.put(vacancy.getId(), vacancy);
        return vacancy;
    }

    @Override
    public void deleteById(int id) {
        vacancies.remove(id);
    }

    @Override
    public boolean update(Vacancy vacancy) {
        return vacancies.computeIfPresent(vacancy.getId(), (id, oldVacancy) -> new Vacancy(oldVacancy.getId(),
                vacancy.getTitle(),
                vacancy.getDescription(),
                vacancy.getCreationDate())) != null;
    }

    @Override
    public Optional<Vacancy> findById(int id) {
        return Optional.ofNullable(vacancies.get(id));
    }

    @Override
    public Collection<Vacancy> findAll() {
        return vacancies.values();
    }
}