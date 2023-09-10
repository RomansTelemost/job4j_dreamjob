package ru.job4j.dreamjob.repository;

import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Repository;
import ru.job4j.dreamjob.model.Candidate;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Repository
@ThreadSafe
public class MemoryCandidateRepository implements CandidateRepository {

    private AtomicInteger nextId = new AtomicInteger(0);
    private final Map<Integer, Candidate> candidates = new ConcurrentHashMap<>();

    public MemoryCandidateRepository() {
        save(new Candidate(0, "Ivan", "Good knowledge",
                LocalDateTime.of(2023, 9, 4, 10, 0),
                1, 0));
        save(new Candidate(0, "Stepan", "Not bad knowledge",
                LocalDateTime.of(2023, 9, 4, 10, 0),
                2, 0));
        save(new Candidate(0, "Semen", "Poor knowledge",
                LocalDateTime.of(2023, 9, 4, 10, 0),
                3, 0));
    }

    @Override
    public Candidate save(Candidate vacancy) {
        vacancy.setId(nextId.incrementAndGet());
        candidates.put(vacancy.getId(), vacancy);
        return vacancy;
    }

    @Override
    public boolean deleteById(int id) {
        return candidates.remove(id) != null;
    }

    @Override
    public boolean update(Candidate candidate) {
        return candidates.computeIfPresent(candidate.getId(), (id, oldCandidate) -> new Candidate(oldCandidate.getId(),
                candidate.getName(),
                candidate.getDescription(),
                candidate.getCreationDate(),
                candidate.getCityId(),
                candidate.getFileId())) != null;
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
