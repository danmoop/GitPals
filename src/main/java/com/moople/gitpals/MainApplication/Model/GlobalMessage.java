package com.moople.gitpals.MainApplication.Model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "Global Message")
public class GlobalMessage {
    @Id
    private String id;
    private String content;

    public GlobalMessage(String content) {
        this.content = content;
    }
}