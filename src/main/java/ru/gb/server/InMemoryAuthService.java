package ru.gb.server;

import java.util.ArrayList;
import java.util.List;

public class InMemoryAuthService implements AuthService {
    private final List<Userdata> users;

    public InMemoryAuthService() {
        users = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            users.add(new Userdata("login" + i, "pass" + i, "nick" + i));
        }

    }


    @Override
    public String getNickByLoginAndPassword(String login, String Password) {
        for (Userdata user : users) {
            if (user.getLogin().equals(login) && user.getPassword().equals(Password)) {
                return user.getNick();
            }
        }
        return null;
    }

    private static class Userdata {
        private final String login;
        private final String password;
        private final String nick;

        public Userdata(String login, String password, String nick) {
            this.login = login;
            this.password = password;
            this.nick = nick;
        }

        public String getLogin() {
            return login;
        }

        public String getPassword() {
            return password;
        }

        public String getNick() {
            return nick;
        }
    }
}
