package com.moople.gitpals.MainApplication.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DialogPair {
    private int unreadMessages;
    private List<Message> messages;
}