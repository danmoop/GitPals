package com.moople.gitpals.MainApplication.model;

import com.moople.gitpals.MainApplication.tools.Encrypt;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@Data
@Document(collection = "keyStorage")
public class KeyStorage {

    @Id
    private String id;

    private String username;
    private String key;

    public KeyStorage(String username) {
        this.username = username;
        this.key = generateKey();
    }

    public String generateKey() {
        return Encrypt.MD5(username + UUID.randomUUID().toString() + Math.random());
    }
}