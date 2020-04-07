package com.moople.gitpals.MainApplication.Service;

import com.moople.gitpals.MainApplication.Model.KeyStorage;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Service;

@Service
public interface KeyStorageInterface extends MongoRepository<KeyStorage, String> {
    KeyStorage findByUsername(String username);
}