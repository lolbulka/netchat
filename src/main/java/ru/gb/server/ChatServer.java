package ru.gb.server;


import ru.gb.Command;
import ru.gb.JdbcWork;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.stream.Collectors;

import static ru.gb.Command.*;

public class ChatServer {
    private final Map<String, ClientHandler> clients;
    private final AuthService authService;
    private final JdbcWork jdbcWork;

    public AuthService getAuthService() {
        return authService;
    }

    public ChatServer() {
        clients = new HashMap<>();
        authService = new InMemoryAuthService();
        jdbcWork = new JdbcWork();
        jdbcWork.connect();
        //jdbcWork.change();
        //jdbcWork.createTable();
        //jdbcWork.insert();
        //jdbcWork.select();
    }

    public boolean isNickBusy(String nick) {
        return clients.containsKey(nick);
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(8189)) {
            while (true) {
                System.out.println("Ждем подключения клиента...");
                final Socket socket = serverSocket.accept();
                new ClientHandler(socket, this, jdbcWork);
                System.out.println("Клиент подключился.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void subscribe(ClientHandler client) {
        clients.put(client.getNick(), client);
        broadcastClientList();
    }

    public void unsubscribe(ClientHandler client) {
        clients.remove(client.getNick());
        broadcastClientList();
    }

    public void broadcast(String message) {
        for (ClientHandler client : clients.values()) {
            //System.out.println("отсыл сооб" + message);
            client.sendMessage(message);
        }
    }

    public void whisp(ClientHandler from, String msg) {
        if (getCommandByText(msg) == END) {
            for (ClientHandler client : clients.values()) {
                if (client.getNick().equals(from.getNick())) {
                    client.sendMessage(END.getCommand() + " " + from);
                    return;
                }
            }
        } else {
            final String[] split = msg.split(" ", 3);
            final String whisper = split[1];
            final String message = split[2];
            for (ClientHandler client : clients.values()) {
                if (client.getNick().equals(whisper)) {
                    client.sendMessage("Сообщение от " + from.getNick() + ": " + message);
                    return;
                }
            }
            from.sendMessage("Участника с ником " + whisper + " нет в чате.");
        }

    }

    public void sendMessageToClient(ClientHandler from, String nickTo, String message) {
        final ClientHandler clientTo = clients.get(nickTo);
        if (clientTo != null) {
            clientTo.sendMessage("От " + from.getNick() + ": " + message);
            from.sendMessage("Участнику " + nickTo + ": " + message);
            return;
        }
        from.sendMessage("Участника с ником " + nickTo + " нет в чате.");
    }

    public void broadcastClientList() {
        final String message = clients.values().stream()
                .map(ClientHandler::getNick)
                .collect(Collectors.joining(" "));
        broadcast(Command.CLIENTS, message);
    }

    private void broadcast(Command command, String message) {
        for (ClientHandler client : clients.values()) {
            client.sendMessage(command, message);
        }
    }

    public void changeNick(ClientHandler user, String newNick) {
        jdbcWork.changeNickInDB(user, newNick);
        clients.remove(user.getNick());
        user.setNick(newNick);
        clients.put(newNick, user);
        broadcastClientList();
        //jdbcWork.select();
    }

    public boolean isNickBusyDB(ClientHandler client, String newNick) {
        return jdbcWork.isNickBusy(client, newNick);
    }
}
