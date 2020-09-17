package com.moople.gitpals.MainApplication.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DialogPair<T,E> {
    private T unreadMessages;
    private E messages;
}