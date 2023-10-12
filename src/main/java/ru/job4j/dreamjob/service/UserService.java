package ru.job4j.dreamjob.service;

import ru.job4j.dreamjob.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    Optional<User> save(User user);

    Optional<User> findByEmail(String email);

    Optional<User> findByEmailAndPassword(String email, String password);

    List<User> findAll();

    boolean deleteById(int id);

    Optional<User> findById(int id);

}
