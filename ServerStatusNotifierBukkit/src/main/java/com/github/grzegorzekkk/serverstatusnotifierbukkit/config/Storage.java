package com.github.grzegorzekkk.serverstatusnotifierbukkit.config;

import com.github.grzegorzekkk.serverstatusnotifierbukkit.ServerStatusNotifierBukkit;
import com.github.grzegorzekkk.serverstatusnotifierbukkit.utils.ConsoleLogger;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;

public abstract class Storage {
    protected FileConfiguration config;

    private String fileName;
    private File configFile;

    public void setConfigFilename(String dataFileName) {
        this.fileName = dataFileName;
    }

    /**
     * Loads config file from disk to plugin memory.
     */
    public void reloadConfig() {
        if (configFile == null) {
            configFile = new File(ServerStatusNotifierBukkit.getInstance().getDataFolder(), fileName);
        }
        if (!configFile.exists()) {
            ServerStatusNotifierBukkit.getInstance().saveResource(fileName, false);
        }
        config = YamlConfiguration.loadConfiguration(configFile);

        // Look for defaults in the jar
        Reader defConfigStream = null;
        try {defConfigStream = new InputStreamReader(ServerStatusNotifierBukkit.getInstance().getResource(fileName), "UTF8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        if (defConfigStream != null) {
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
            config.setDefaults(defConfig);
        }
    }

    /**
     * Loads config file from disk to plugin memory.
     */
    public FileConfiguration getConfig() {
        if (config == null) reloadConfig();
        return config;
    }

    /**
     * Saves configuration entries to disk.
     */
    public void saveConfig() {
        if (config == null || configFile == null) {
            return;
        }
        try {
            getConfig().save(configFile);
        } catch (IOException ex) {
            ConsoleLogger.warn("Could not save config to " + configFile + ex.toString());
        }
    }
}
