package com.github.grzegorzekkk.serverstatusnotifierbukkit.server;

import com.github.grzegorzekkk.serverstatusnotifier.serverstatusnotifiermodel.ServerDetails;
import com.github.grzegorzekkk.serverstatusnotifier.serverstatusnotifiermodel.SsnJsonMessage;
import com.github.grzegorzekkk.serverstatusnotifierbukkit.ServerStatusNotifierBukkit;
import com.github.grzegorzekkk.serverstatusnotifierbukkit.config.PluginConfig;
import com.github.grzegorzekkk.serverstatusnotifierbukkit.config.messages.Message;
import com.github.grzegorzekkk.serverstatusnotifierbukkit.config.messages.MessagesConfig;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.logging.LogRecord;

@Data
public class NotifierServer implements ConsoleHandler.ConsoleObservable {
    private ServerSocket serverSocket;
    private Set<UUID> clientsIdentifiers;
    private List<Socket> consoleListeners;

    public NotifierServer(int port) {
        try {
            serverSocket = ServerSocketFactory.getDefault().createServerSocket(port);
            ConsoleLogger.info(String.format(MessagesConfig.getInstance().getMessage(Message.NOTIFIER_ENABLED), port));
            clientsIdentifiers = new HashSet<>();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startListening() {
        try {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                ConsoleLogger.info(String.format(MessagesConfig.getInstance().getMessage(Message.INCOMING_CONN),
                        clientSocket.getRemoteSocketAddress().toString()));
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        listenForClientMessages(clientSocket);
                    }
                }.runTaskAsynchronously(ServerStatusNotifierBukkit.getInstance());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void listenForClientMessages(Socket clientSocket) {
        try (BufferedReader inReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter writer = new PrintWriter(clientSocket.getOutputStream())) {

            for (String jsonString = inReader.readLine(); jsonString != null; jsonString = inReader.readLine()) {
                SsnJsonMessage<String> incomingMessage = SsnJsonMessage.fromJsonString(jsonString, String.class);
                SsnJsonMessage.MessageType status = incomingMessage.getStatus();
                UUID clientId = incomingMessage.getClientId();

                switch (status) {
                    case AUTH_REQUEST:
                        handleClientAuth(jsonString, writer, clientSocket.getRemoteSocketAddress().toString());
                        break;
                    case DATA_REQUEST:
                        handleDataRequest(clientId, writer);
                        break;
                    case CONSOLE_REQUEST:
                        handleConsoleRequest(clientId, clientSocket);
                        break;
                    default:
                        break;
                }

                writer.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleClientAuth(String jsonString, PrintWriter writer, String clientSocketAddress) {
        SsnJsonMessage<String> ssnAuthRequest = SsnJsonMessage.fromJsonString(jsonString, String.class);

        SsnJsonMessage<Object> ssnAuthResponse = new SsnJsonMessage<>();
        if (PluginConfig.getInstance().getPassword().equals(ssnAuthRequest.getData())) {
            clientsIdentifiers.add(ssnAuthRequest.getClientId());
            ssnAuthResponse.setStatus(SsnJsonMessage.MessageType.AUTHORIZED_RESPONSE);

            ConsoleLogger.info(String.format(MessagesConfig.getInstance().getMessage(Message.PASSWORD_CORRECT),
                    clientSocketAddress));
        } else {
            ssnAuthResponse.setStatus(SsnJsonMessage.MessageType.UNAUTHORIZED_RESPONSE);

            ConsoleLogger.info(String.format(MessagesConfig.getInstance().getMessage(Message.PASSWORD_INCORRECT),
                    clientSocketAddress));
        }
        writer.println(ssnAuthResponse.toJsonString());
    }

    private void handleDataRequest(UUID clientId, PrintWriter writer) {
        if (clientsIdentifiers.contains(clientId)) {
            SsnJsonMessage<ServerDetails> ssnDataResponse = new SsnJsonMessage<>();
            ServerDetails srvDetails = fetchCurrentServerDetails();

            ssnDataResponse.setStatus(SsnJsonMessage.MessageType.DATA_RESPONSE);
            ssnDataResponse.setData(srvDetails);

            writer.println(ssnDataResponse.toJsonString());
        }
    }

    private void handleConsoleRequest(UUID clientId, Socket consoleListener) {
        if (clientsIdentifiers.isEmpty()) {
            ConsoleHandler consoleHandler = new ConsoleHandler();
            consoleHandler.setObserver(this);
            Bukkit.getLogger().addHandler(consoleHandler);
        }
        if (clientsIdentifiers.contains(clientId)) {
            consoleListeners.add(consoleListener);
        }
    }

    @Override
    public void onRecordPublish(LogRecord record) {
        SsnJsonMessage<String> ssnConsoleResponse = new SsnJsonMessage<>();
        ssnConsoleResponse.setStatus(SsnJsonMessage.MessageType.CONSOLE_RESPONSE);
        ssnConsoleResponse.setData(record.toString());

        consoleListeners.stream().filter(s -> s.isConnected() && !s.isOutputShutdown()).forEach(socket -> {
            try (PrintWriter writer = new PrintWriter(socket.getOutputStream())) {
                writer.println(ssnConsoleResponse);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
    }

    private ServerDetails fetchCurrentServerDetails() {
        ServerDetails serverDetails = new ServerDetails();
        serverDetails.setServerName(Bukkit.getServerName());
        serverDetails.setPlayersCount(Bukkit.getOnlinePlayers().size());
        serverDetails.setOnline(true);
        serverDetails.setServerVersion(Bukkit.getVersion());
        serverDetails.setPlayersMax(Bukkit.getMaxPlayers());

        return serverDetails;
    }
}
