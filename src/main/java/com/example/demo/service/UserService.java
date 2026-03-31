package com.example.demo.service;

import com.example.demo.entity.User;

import java.util.List;

public interface UserService {

    User save(User user);

    void delete(Long id);

    User update(Long id, User user);

    User findById(Long id);

    List<User> findAll();
}
