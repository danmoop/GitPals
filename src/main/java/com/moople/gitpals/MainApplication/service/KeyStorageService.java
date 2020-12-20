package com.moople.gitpals.MainApplication.service;

import com.moople.gitpals.MainApplication.model.KeyStorage;
import com.moople.gitpals.MainApplication.repository.KeyStorageRepository;
import com.moople.gitpals.MainApplication.service.interfaces.KeyStorageInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KeyStorageService implements KeyStorageInterface {

    @Autowired
    private KeyStorageRepository keyStorageRepository;

    @Override
    public KeyStorage findByUsername(String username) {
        return keyStorageRepository.findByUsername(username);
    }

    @Override
    public List<KeyStorage> findAll() {
        return keyStorageRepository.findAll();
    }
}

