package com.moople.gitpals.MainApplication.repository;

import com.moople.gitpals.MainApplication.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserRepository extends MongoRepository<User, String> {
    User findByUsername(String username);

    List<User> findAll();
}