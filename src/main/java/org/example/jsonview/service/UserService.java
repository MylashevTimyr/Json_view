package org.example.jsonview.service;

import org.example.jsonview.model.User;

import java.util.List;

public interface UserService {
    List<User> findAll();

    User findById(Long id);

    User save(User user);

    void deleteById(Long id);
}
