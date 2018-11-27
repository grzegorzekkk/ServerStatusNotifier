package com.github.grzegorzekkk.serverstatusnotifierbukkit.config.messages;

import com.github.grzegorzekkk.serverstatusnotifierbukkit.config.Storage;

public class MessagesConfig extends Storage {

    private static MessagesConfig instance;

    private MessagesConfig() {
    }

    public static MessagesConfig getInstance() {
        if (instance == null) {
            instance = new MessagesConfig();
        }
        return instance;
    }

    public String getMessage(Message m) {
        return config.getString(m.getKey());
    }
}
