package com.moople.gitpals.MainApplication.service.interfaces;

import com.moople.gitpals.MainApplication.model.KeyStorage;

import java.util.List;

public interface KeyStorageInterface {
    KeyStorage findByUsername(String username);

    List<KeyStorage> findAll();

    void save(KeyStorage keyStorage);
}
