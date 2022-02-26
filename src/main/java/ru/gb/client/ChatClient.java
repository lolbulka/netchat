package ru.gb.client;

import javafx.application.Platform;
import ru.gb.HistoryHandler;
import ru.gb.NetChatController;
import ru.gb.server.ChatServer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import static ru.gb.Command.*;

public class ChatClient {
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private final NetChatController controller;
    private final HistoryHandler historyHandler;
    private String nick;

    public ChatClient(NetChatController controller, HistoryHandler historyHandler) {
        this.controller = controller;
        this.historyHandler = historyHandler;
        openConnection();
    }

    public String getNick() {
        return nick;
    }

    private void openConnection() {
        try {
            socket = new Socket("localhost", 8189);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            new Thread(() -> {
                try {
                    while (true) {
                        final String authMsg = in.readUTF();
                        if (getCommandByText(authMsg) == AUTHOK) {
                            nick = authMsg.split(" ")[1];
                            controller.setAuth(true);
                            historyHandler.useFile();  //подключим запись в файл
                            historyHandler.readFile(controller); // загрузим историю чата
                            controller.addMessage("Успешная авторизация под ником " + nick);
                            break;
                        } else {
                            controller.errMessage(authMsg);
                        }
                    }
                    while (true) {
                        final String message = in.readUTF();
                        if (getCommandByText(message) == END) {
                            controller.setAuth(false);
                            break;
                        }
                        if (getCommandByText(message) == CLIENTS) {
                            final String[] clients = message.replace(CLIENTS.getCommand() + " ", "").split(" ");
                            Platform.runLater(() -> controller.updateClientList(clients));
                        } else {
                            controller.addMessage(message);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    closeConnection();
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeConnection() {
        if (in != null) {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (out != null) {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public void sendMessage(String message) {
        try {
            out.writeUTF(message);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
