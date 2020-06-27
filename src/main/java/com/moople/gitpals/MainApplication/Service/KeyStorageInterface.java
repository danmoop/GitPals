package com.moople.gitpals.MainApplication.Service;

import com.moople.gitpals.MainApplication.Model.KeyStorage;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface KeyStorageInterface extends MongoRepository<KeyStorage, String> {
    KeyStorage findByUsername(String username);

    List<KeyStorage> findAll();
}