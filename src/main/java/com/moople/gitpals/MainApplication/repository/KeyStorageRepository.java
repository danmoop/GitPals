package com.moople.gitpals.MainApplication.repository;

import com.moople.gitpals.MainApplication.model.KeyStorage;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface KeyStorageRepository extends MongoRepository<KeyStorage, String> {
    KeyStorage findByUsername(String username);

    List<KeyStorage> findAll();
}