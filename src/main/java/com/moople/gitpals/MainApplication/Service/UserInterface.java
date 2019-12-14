package com.moople.gitpals.MainApplication.Service;

import com.moople.gitpals.MainApplication.Model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserInterface extends MongoRepository<User, String> {
    User findByUsername(String username);
    List<User> findAll();
}