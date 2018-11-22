package com.github.grzegorzekkk.serverstatusnotifierbukkit.server;

import com.github.grzegorzekkk.serverstatusnotifierbukkit.config.PluginConfig;
import com.github.grzegorzekkk.serverstatusnotifierbukkit.model.ServerDetails;
import com.github.grzegorzekkk.serverstatusnotifierbukkit.model.ServerStatus;
import com.github.grzegorzekkk.serverstatusnotifierbukkit.ServerStatusNotifierBukkit;
import com.github.grzegorzekkk.serverstatusnotifierbukkit.model.SsnJsonMessage;
import com.github.grzegorzekkk.serverstatusnotifierbukkit.utils.ConsoleLogger;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import javax.net.ServerSocketFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

@Data
public class NotifierServer {
    private ServerSocket serverSocket;

    public NotifierServer(int port) {
        try {
            serverSocket = ServerSocketFactory.getDefault().createServerSocket(port);
            ConsoleLogger.info("Started notifier server on port: " + port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startListening() {
        try {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        handleClient(clientSocket);
                    }
                }.runTaskAsynchronously(ServerStatusNotifierBukkit.getInstance());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleClient(Socket clientSocket) {
        try (BufferedReader inReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter writer = new PrintWriter(clientSocket.getOutputStream())) {

            SsnJsonMessage<String> ssnAuthRequest = SsnJsonMessage.fromJsonString(inReader.readLine(), String.class);
            if (PluginConfig.getInstance().getPassword().equals(ssnAuthRequest.getData())) {
                String serverName = Bukkit.getServerName();
                int playersOnline = Bukkit.getOnlinePlayers().size();

                ServerStatus serverStatus = new ServerStatus(serverName, true);
                SsnJsonMessage<ServerDetails> ssnDataResponse = new SsnJsonMessage<>();
                ssnDataResponse.setData(new ServerDetails(serverStatus, playersOnline));
                ssnDataResponse.setStatus(SsnJsonMessage.MessageType.DATA_RESPONSE);

                writer.println(ssnDataResponse.toJsonString());
            } else {
                SsnJsonMessage<Object> ssnUnauthResponse = new SsnJsonMessage<>();
                ssnUnauthResponse.setStatus(SsnJsonMessage.MessageType.UNAUTHORIZED_RESPONSE);

                writer.println(ssnUnauthResponse.toJsonString());
            }

            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
