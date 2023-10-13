package ru.job4j.dreamjob.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.sql2o.Connection;
import org.sql2o.Query;
import org.sql2o.Sql2o;
import ru.job4j.dreamjob.model.User;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class Sql2oUserRepository implements UserRepository {

    private static final Logger LOG = LoggerFactory.getLogger(User.class.getName());

    private final Sql2o sql2o;

    public Sql2oUserRepository(Sql2o sql2o) {
        this.sql2o = sql2o;
    }

    @Override
    public Optional<User> save(User user) {
        try (var connection = sql2o.open()) {
            var query = connection.createQuery("INSERT INTO users (name, email, password) VALUES (:name, :email, :password)", true)
                    .addParameter("name", user.getName())
                    .addParameter("email", user.getEmail())
                    .addParameter("password", user.getPassword());
            int generatedId = query.executeUpdate().getKey(Integer.class);
            user.setId(generatedId);
            return Optional.of(user);
        } catch (Exception exception) {
            LOG.error(exception.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public Optional<User> findByEmail(String email) {
        try (var connection = sql2o.open()) {
            var query = connection.createQuery("SELECT * FROM users WHERE email = :email");
            query.addParameter("email", email);
            var user = query.setColumnMappings(User.COLUMN_MAPPING).executeAndFetchFirst(User.class);
            if (Objects.isNull(user)) {
                return Optional.empty();
            }
            return Optional.of(user);
        }
    }

    @Override
    public Optional<User> findByEmailAndPassword(String email, String password) {
        try (var connection = sql2o.open()) {
            var query = connection.createQuery("SELECT * FROM users WHERE email = :email and password = :password");
            query.addParameter("email", email);
            query.addParameter("password", password);
            var user = query.setColumnMappings(User.COLUMN_MAPPING).executeAndFetchFirst(User.class);
            if (Objects.isNull(user)) {
                return Optional.empty();
            }
            return Optional.of(user);
        }
    }

    @Override
    public List<User> findAll() {
        try (var connection = sql2o.open()) {
            var query = connection.createQuery("SELECT * FROM users");
            return query.setColumnMappings(User.COLUMN_MAPPING).executeAndFetch(User.class);
        } catch (Exception exception) {
            LOG.error(exception.getMessage());
            throw exception;
        }
    }

    @Override
    public boolean deleteById(int id) {
        try (var connection = sql2o.open()) {
            Query query = connection.createQuery("DELETE FROM users WHERE id = :id");
            query.addParameter("id", id);
            return query.executeUpdate().getResult() > 0;
        } catch (Exception e) {
            LOG.error(e.getMessage());
            return false;
        }
    }

    @Override
    public Optional<User> findById(int id) {
        try (Connection connection = sql2o.open()) {
            Query query = connection.createQuery("SELECT * FROM users WHERE id = :id");
            query.addParameter("id", id);
            User user = query.setColumnMappings(User.COLUMN_MAPPING).executeAndFetchFirst(User.class);
            return Optional.ofNullable(user);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            return Optional.empty();
        }
    }
}
