package com.github.grzegorzekkk.serverstatusnotifierbukkit.config;

/**
 * Container class used to store Configuration entries of plugin.
 */
public class PluginConfig extends Storage {

    private static PluginConfig instance;

    private PluginConfig() {
    }

    public static PluginConfig getInstance() {
        if (instance == null) {
            instance = new PluginConfig();
        }
        return instance;
    }

    public int getServerPort() {
        return config.getInt("server_port");
    }

    public String getPassword() {
        return config.getString("password");
    }
}
