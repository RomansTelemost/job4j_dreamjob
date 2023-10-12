package ru.job4j.dreamjob.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.job4j.dreamjob.configuration.DatasourceConfiguration;
import ru.job4j.dreamjob.model.User;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

import static java.util.Collections.emptyList;
import static java.util.Optional.empty;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class Sql2oUserRepositoryTest {

    private static Sql2oUserRepository sql2oUserRepository;

    @BeforeAll
    public static void initRepositories() throws Exception {
        var properties = new Properties();
        try (InputStream inputStream = Sql2oUserRepository.class.getClassLoader().getResourceAsStream("connection.properties")) {
            properties.load(inputStream);
        }
        var url = properties.getProperty("datasource.url");
        var username = properties.getProperty("datasource.username");
        var password = properties.getProperty("datasource.password");

        var configuration = new DatasourceConfiguration();
        var datasource = configuration.connectionPool(url, username, password);
        var sql2o = configuration.databaseClient(datasource);

        sql2oUserRepository = new Sql2oUserRepository(sql2o);
    }

    @AfterEach
    public void clearRepositories() {
        List<User> users = sql2oUserRepository.findAll();
        for (User user : users) {
            sql2oUserRepository.deleteById(user.getId());
        }
    }

    @Test
    public void whenSaveThenGetSame() {
        User user = new User("Ivan", "ivan email", "ivan pass");
        sql2oUserRepository.save(user);
        Optional<User> userOptional = sql2oUserRepository.findById(user.getId());
        Assertions.assertThat(userOptional.isPresent()).isTrue();
        Assertions.assertThat(user).isEqualTo(userOptional.get());
    }

    @Test
    public void whenSaveSeveralThenGetAll() {
        User user1 = new User("Ivan1", "ivan email 1", "ivan pass 1");
        User user2 = new User("Ivan2", "ivan email 2", "ivan pass 2");
        User user3 = new User("Ivan3", "ivan email 3", "ivan pass 3");
        sql2oUserRepository.save(user1);
        sql2oUserRepository.save(user2);
        sql2oUserRepository.save(user3);
        Assertions.assertThat(sql2oUserRepository.findAll()).containsAll(List.of(user1, user2, user3));
    }

    @Test
    public void whenDontSaveThenNothingFound() {
        assertThat(sql2oUserRepository.findAll()).isEqualTo(emptyList());
        assertThat(sql2oUserRepository.findById(0)).isEqualTo(empty());
    }

    @Test
    public void whenDeleteThenGetEmptyOptional() {
        Optional<User> userOptional = sql2oUserRepository.save(new User("Ivan1", "ivan email 1", "ivan pass 1"));
        Assertions.assertThat(userOptional.isPresent()).isTrue();
        var isDeleted = sql2oUserRepository.deleteById(userOptional.get().getId());
        var savedCandidate = sql2oUserRepository.findById(userOptional.get().getId());
        assertThat(isDeleted).isTrue();
        assertThat(savedCandidate).isEqualTo(empty());
    }

    @Test
    public void whenDeleteByInvalidIdThenGetFalse() {
        assertThat(sql2oUserRepository.deleteById(0)).isFalse();
    }

    @Test
    public void whenSaveThenFindBySameEmail() {
        User user1 = new User("Ivan1", "ivan email 1", "ivan pass 1");
        sql2oUserRepository.save(user1);
        Optional<User> findByEmailUserOpt = sql2oUserRepository.findByEmail(user1.getEmail());
        Assertions.assertThat(findByEmailUserOpt.isPresent()).isTrue();
        Assertions.assertThat(findByEmailUserOpt.get()).isEqualTo(user1);
    }

    @Test
    public void whenSaveThenFindBySameEmailAndPassword() {
        User user1 = new User("Ivan1", "ivan email 1", "ivan pass 1");
        sql2oUserRepository.save(user1);
        Optional<User> findByEmailUserOpt = sql2oUserRepository.findByEmailAndPassword(user1.getEmail(), user1.getPassword());
        Assertions.assertThat(findByEmailUserOpt.isPresent()).isTrue();
        Assertions.assertThat(findByEmailUserOpt.get()).isEqualTo(user1);
    }
}