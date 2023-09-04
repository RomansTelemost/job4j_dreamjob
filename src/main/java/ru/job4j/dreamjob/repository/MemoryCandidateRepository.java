package ru.job4j.dreamjob.repository;

import org.springframework.stereotype.Repository;
import ru.job4j.dreamjob.model.Candidate;
import ru.job4j.dreamjob.model.Vacancy;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class MemoryCandidateRepository implements CandidateRepository {

    private static final MemoryCandidateRepository INSTANCE = new MemoryCandidateRepository();

    private final Map<Integer, Candidate> candidates = new HashMap<>();

    private int nextId = 1;

    public MemoryCandidateRepository() {
        save(new Candidate(0, "Ivan", "Good knowledge", LocalDateTime.of(2023, 9, 4, 10, 0)));
        save(new Candidate(0, "Stepan", "Not bad knowledge", LocalDateTime.of(2023, 9, 4, 10, 0)));
        save(new Candidate(0, "Semen", "Poor knowledge", LocalDateTime.of(2023, 9, 4, 10, 0)));
    }

    public static MemoryCandidateRepository getInstance() {
        return INSTANCE;
    }

    @Override
    public Candidate save(Candidate vacancy) {
        vacancy.setId(nextId++);
        candidates.put(vacancy.getId(), vacancy);
        return vacancy;
    }

    @Override
    public void deleteById(int id) {
        candidates.remove(id);
    }

    @Override
    public boolean update(Candidate candidate) {
        return candidates.computeIfPresent(candidate.getId(), (id, oldCandidate) -> new Candidate(oldCandidate.getId(),
                candidate.getName(),
                candidate.getDescription(),
                candidate.getCreationDate())) != null;
    }

    @Override
    public Optional<Candidate> findById(int id) {
        return Optional.ofNullable(candidates.get(id));
    }

    @Override
    public Collection<Candidate> findAll() {
        return candidates.values();
    }
}
