package com.github.grzegorzekkk.serverstatusnotifierbukkit.config.messages;

public enum Message {
    PLUGIN_ENABLED("enabled"),
    PLUGIN_DISABLED("disabled"),
    NOTIFIER_ENABLED("notifier_enabled"),
    INCOMING_CONN("incoming_connection"),
    PASSWORD_CORRECT("password_correct"),
    PASSWORD_INCORRECT("password_incorrect"),
    NO_PERMISSION("no_permission");

    private String key;

    Message(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
