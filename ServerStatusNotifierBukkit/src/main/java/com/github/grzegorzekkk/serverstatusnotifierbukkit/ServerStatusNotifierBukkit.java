package com.github.grzegorzekkk.serverstatusnotifierbukkit;

import com.github.grzegorzekkk.serverstatusnotifierbukkit.config.PluginConfig;
import com.github.grzegorzekkk.serverstatusnotifierbukkit.server.NotifierServer;
import com.github.grzegorzekkk.serverstatusnotifierbukkit.utils.ConsoleLogger;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class ServerStatusNotifierBukkit extends JavaPlugin {
    private static ServerStatusNotifierBukkit instance;
    private static PluginConfig config;

    @Override
    public void onEnable() {
        instance = this;
        ConsoleLogger.setLogger(getLogger());
        config = PluginConfig.getInstance();
        config.setConfigFilename("config.yml");
        config.reloadConfig();
        startNotifierServerListener();
        ConsoleLogger.info("Enabled ssnb plugin");
    }

    @Override
    public void onDisable() {
        ConsoleLogger.info("disabled ssnb plugin");
    }

    private void startNotifierServerListener() {
        new BukkitRunnable() {
            @Override
            public void run() {
                NotifierServer ns = new NotifierServer(config.getServerPort());
                ns.startListening();
            }
        }.runTaskAsynchronously(this);
    }

    public static ServerStatusNotifierBukkit getInstance() {
        return instance;
    }
}
