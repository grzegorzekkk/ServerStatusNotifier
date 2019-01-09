package com.github.grzegorzekkk.serverstatusnotifierbukkit.server;

import com.github.grzegorzekkk.serverstatusnotifier.serverstatusnotifiermodel.ServerDetails;
import com.github.grzegorzekkk.serverstatusnotifier.serverstatusnotifiermodel.SsnJsonMessage;
import com.github.grzegorzekkk.serverstatusnotifierbukkit.ServerStatusNotifierBukkit;
import com.github.grzegorzekkk.serverstatusnotifierbukkit.config.PluginConfig;
import com.github.grzegorzekkk.serverstatusnotifierbukkit.config.messages.Message;
import com.github.grzegorzekkk.serverstatusnotifierbukkit.config.messages.MessagesConfig;
import com.github.grzegorzekkk.serverstatusnotifierbukkit.server.exception.UnauthenticatedClientException;
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
import java.util.*;

@Data
public class NotifierServer implements ConsoleAppender.ConsoleObservable {
    private ConsoleAppender consoleAppender;
    private ServerSocket serverSocket;
    private Set<UUID> clientsIdentifiers;
    private List<Socket> consoleListeners = new LinkedList<>();

    public NotifierServer(int port) {
        try {
            serverSocket = ServerSocketFactory.getDefault().createServerSocket(port);
            ConsoleLogger.info(String.format(MessagesConfig.getInstance().getMessage(Message.NOTIFIER_ENABLED), port));
            clientsIdentifiers = new HashSet<>();

            consoleAppender = new ConsoleAppender();
            consoleAppender.setObserver(this);
            consoleAppender.start();
            ServerStatusNotifierBukkit.ROOT_LOGGER.addAppender(consoleAppender);
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
        try {
            BufferedReader inReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter writer = new PrintWriter(clientSocket.getOutputStream());

            for (String jsonString = inReader.readLine(); jsonString != null; jsonString = inReader.readLine()) {
                SsnJsonMessage<String> incomingMessage = SsnJsonMessage.fromJsonString(jsonString, String.class);
                SsnJsonMessage.MessageType status = incomingMessage.getStatus();
                UUID clientId = incomingMessage.getClientId();

                if (status != SsnJsonMessage.MessageType.AUTH_REQUEST && !clientsIdentifiers.contains(clientId))
                    throw new UnauthenticatedClientException();

                switch (status) {
                    case AUTH_REQUEST:
                        handleClientAuth(jsonString, writer, clientSocket.getRemoteSocketAddress().toString());
                        break;
                    case DATA_REQUEST:
                        handleDataRequest(writer);
                        break;
                    case CONSOLE_REQUEST:
                        handleConsoleRequest(clientSocket);
                        break;
                    case CONSOLE_COMMAND:
                        handleConsoleCommand(jsonString);
                        break;
                    default:
                        break;
                }

                writer.flush();
            }
        } catch (UnauthenticatedClientException | IOException e) {
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

    private void handleDataRequest(PrintWriter writer) {
        SsnJsonMessage<ServerDetails> ssnDataResponse = new SsnJsonMessage<>();
        ServerDetails srvDetails = fetchCurrentServerDetails();

        ssnDataResponse.setStatus(SsnJsonMessage.MessageType.DATA_RESPONSE);
        ssnDataResponse.setData(srvDetails);

        writer.println(ssnDataResponse.toJsonString());
    }

    private void handleConsoleRequest(Socket consoleListener) {
        consoleListeners.add(consoleListener);
    }

    private void handleConsoleCommand(String jsonString) {
        SsnJsonMessage<String> ssnConsoleCommand = SsnJsonMessage.fromJsonString(jsonString, String.class);

        String command = ssnConsoleCommand.getData();
        Bukkit.getScheduler().callSyncMethod(ServerStatusNotifierBukkit.getInstance(), () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command));
    }

    @Override
    public void onLogPublish(String logMessage) {
        SsnJsonMessage<String> ssnConsoleResponse = new SsnJsonMessage<>();
        ssnConsoleResponse.setStatus(SsnJsonMessage.MessageType.CONSOLE_RESPONSE);
        ssnConsoleResponse.setData(logMessage);

        consoleListeners.stream().filter(s -> !s.isClosed() && !s.isOutputShutdown()).forEach(socket -> {
            try {
                PrintWriter writer = new PrintWriter(socket.getOutputStream());
                writer.println(ssnConsoleResponse.toJsonString());
                writer.flush();
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
