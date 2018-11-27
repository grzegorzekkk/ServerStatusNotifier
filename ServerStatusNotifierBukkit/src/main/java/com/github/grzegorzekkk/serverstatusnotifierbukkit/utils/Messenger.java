package com.github.grzegorzekkk.serverstatusnotifierbukkit.utils;

import com.github.grzegorzekkk.serverstatusnotifierbukkit.config.messages.Message;
import com.github.grzegorzekkk.serverstatusnotifierbukkit.config.messages.MessagesConfig;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.Arrays;

public class Messenger {

    private Messenger() {
    }

    public static void broadcast(String msg) {
        Bukkit.broadcastMessage(formatMsg(msg));
    }

    public static void broadcast(Message m) {
        Bukkit.broadcastMessage(formatMsg(MessagesConfig.getInstance().getMessage(m)));
    }

    public static void send(final CommandSender sender, String msg) {
        sender.sendMessage(formatMsg(msg));
    }

    public static void send(final CommandSender sender, String[] msgs) {
        Arrays.stream(msgs).forEach(a -> sender.sendMessage(formatMsg(a)));
    }

    public static void send(final CommandSender sender, Message m) {
        sender.sendMessage(formatMsg(MessagesConfig.getInstance().getMessage(m)));
    }

    public static void send(final CommandSender sender, Message... m) {
        Arrays.stream(m).forEach(a -> sender.sendMessage(formatMsg(MessagesConfig.getInstance().getMessage(a))));
    }

    private static String formatMsg(String msg) {
        return ChatColor.translateAlternateColorCodes('&', msg);
    }
}