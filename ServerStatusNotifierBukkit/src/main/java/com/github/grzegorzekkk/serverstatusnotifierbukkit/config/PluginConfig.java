package com.github.grzegorzekkk.serverstatusnotifierbukkit.config;

import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public String getProviderURL() {
        return config.getString("provider.url");
    }

    public String getValidCodeResponse() {
        return config.getString("provider.resp_valid");
    }

//    public List<SmsService> getProviderServices() {
//        List<SmsService> services = new ArrayList<>();
//
//        ConfigurationSection cs = config.getConfigurationSection("provider.services");
//
//        for (String key : cs.getKeys(false)) {
//            services.add(
//                    new SmsService(
//                            cs.getInt(key + ".points"),
//                            cs.getInt(key + ".number"),
//                            cs.getString(key + ".prefix"),
//                            cs.getDouble(key + ".price")));
//        }
//
//        return services;
//    }

    public int getServerPort() {
        return config.getInt("server_port");
    }

    public String getPassword() {
        return config.getString("password");
    }
}
