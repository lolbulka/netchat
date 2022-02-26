package ru.gb;

import ru.gb.server.ClientHandler;

import java.sql.*;

public class JdbcWork {
    private Connection connection;
    private Statement statement;
    private ResultSet resultSet;

    public void connect(){
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:chat.db");
            statement = connection.createStatement();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
    public void disconnect(){
        if (connection !=null){
            try {
                connection.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }
    public void createTable(){
        try {
            statement.executeUpdate("create table if not exists login (login text primary key, password text, " +
                    "nick text)");
            statement.executeUpdate("ALTER TABLE login ADD inUse boolean default false");

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }


    }
/*    public void insert(){
        try {
            statement.executeUpdate("delete from login");
            statement.executeUpdate("insert or ignore into login (login, password, nick) values ('login0', 'pass0', 'Ваня')");
            statement.executeUpdate("insert or ignore into login (login, password, nick) values ('login1', 'pass1', 'Петя')");
            statement.executeUpdate("insert or ignore into login (login, password, nick) values ('login2', 'pass2'," +
                    " 'Бешеный_пес')");
            statement.executeUpdate("insert or ignore into login (login, password, nick) values ('login3', 'pass3', 'Пушка')");
            statement.executeUpdate("insert or ignore into login (login, password, nick) values ('login4', 'pass4', 'Ластик')");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    } */
    public void select(){
        try {
            final ResultSet resultSet = statement.executeQuery("select * from login");
            while (resultSet.next()) {
                String login = resultSet.getString(1);
                String password = resultSet.getString(2);
                String nick = resultSet.getString(3);
                boolean inUSe = resultSet.getBoolean(4);
                System.out.printf("login %s, password %s, nick %s, inUse %b%n\n", login, password, nick, inUSe);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
    public String getNickFromDB(String login, String password){
        try (final PreparedStatement ps = connection.prepareStatement("select nick from login where login = ? " +
                        "and password = ?")){
            ps.setString(1, login);
            ps.setString(2, password);
            resultSet = ps.executeQuery();
            if (resultSet.next()) return resultSet.getString(1);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    public void changeNickInDB(ClientHandler user, String newNick) {
        try (final PreparedStatement ps = connection.prepareStatement("update login set nick = ? where login = ?")) {
            ps.setString(1, newNick);
            ps.setString(2, user.getLogin());
            ps.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void setInUse(String login, boolean inUse) {
        try (final PreparedStatement ps = connection.prepareStatement("update login set inUse = ? where login = ?")) {
            ps.setBoolean(1, inUse);
            ps.setString(2, login);
            ps.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void resetAll() {
        try (final PreparedStatement ps = connection.prepareStatement("update login set inUse = false")) {
            ps.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public boolean isInUse(String login) {
        try (final PreparedStatement ps = connection.prepareStatement("select inUse from login where login = ?")) {
            ps.setString(1, login);
            resultSet = ps.executeQuery();
            if (resultSet.next()) return resultSet.getBoolean(1);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return true;
    }

    public boolean isNickBusy(ClientHandler client, String newNick) {
        try (final PreparedStatement ps = connection.prepareStatement("select nick from login where nick = ?")) {
            ps.setString(1, newNick);
            resultSet = ps.executeQuery();
            if (resultSet.next()) return true;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }
    public void change(){
        try (final PreparedStatement ps = connection.prepareStatement("update login set nick = ? where login = ?")){
            ps.setString(1, "Ролик");
            ps.setString(2, "login0");
            ps.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

}
