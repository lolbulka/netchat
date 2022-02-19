package ru.gb.server;

import ru.gb.Command;
import ru.gb.JdbcWork;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import static ru.gb.Command.*;

public class ClientHandler {

    private final Socket socket;
    private final ChatServer chatServer;
    private final DataInputStream in;
    private final DataOutputStream out;
    private String nick;
    private String login;
    private JdbcWork jdbcWork;


    public ClientHandler(Socket socket, ChatServer chatServer, JdbcWork jdbcWork) {
        try {
            this.nick = "";
            this.login = "";
            this.socket = socket;
            this.chatServer = chatServer;
            this.jdbcWork = jdbcWork;
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            new Thread(() -> {
                try {
                    authenticate();
                    readMessage();
                } finally {
                    closeConnection();
                }

            }).start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }

    private void closeConnection() {
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
                chatServer.unsubscribe(this);
                chatServer.broadcast(nick + " вышел из чата");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void readMessage() {
        try {
            while (true) {
                final String message = in.readUTF();
                if (getCommandByText(message) == END) {
                    chatServer.whisp(this, "/end");
                    break;
                }
                if (getCommandByText(message) == CHANGENICK){
                    final String[] split = message.split(" ");
                    final String newNick = split[1];
                    if (chatServer.isNickBusyDB(this,newNick)) {
                        sendMessage("Пользователь с ником " + newNick + " уже есть в чате");
                        continue;
                    }
                    chatServer.broadcast("Пользователь " + nick + " сменил ник на " + newNick);
                    chatServer.changeNick(this, newNick);
                } else {
                if (getCommandByText(message) == PRIVATE_MESSAGE) {
                    chatServer.whisp(this, message);
                } else
                    chatServer.broadcast(nick + ": " + message);
            }

        }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void authenticate() {
        while (true) {
            try {
                final String message = in.readUTF();
                if (getCommandByText(message) == AUTH) {
                    final String[] split = message.split(" ");
                    final String login = split[1];
                    final String password = split[2];
                    String nick = jdbcWork.getNickFromDB(login, password);
                    if (nick != null) {
                        if (chatServer.isNickBusy(nick)) {
                            sendMessage("Пользователь уже авторизован");
                            continue;
                        }
                        sendMessage(AUTHOK.getCommand() + " " + nick);
                        chatServer.broadcast("Пользователь " + nick + " зашел в чат");
                        this.nick = nick;
                        this.login = login;
                        chatServer.subscribe(this);
                        break;
                    } else {
                        sendMessage("Неверные логин и пароль!");
                    }
                }
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

    public void sendMessage(Command command, String message) {
        try {
            out.writeUTF(command.getCommand() + " " + message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getLogin() {
        return login;
    }
}
