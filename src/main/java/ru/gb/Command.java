package ru.gb;

import java.util.Arrays;

public enum Command {
    AUTHOK("/authok"),
    AUTH("/auth"),
    PRIVATE_MESSAGE("/w"),
    END("/end"),
    CLIENTS("/clients"),
    CHANGENICK("/changeNick"),
    OTHER(" ");

    private final String command;

    Command(String command) {
        this.command = command;
    }

    public static Command getCommandByText(String text) {
        return Arrays.stream(values()).
                filter(cmd -> text.startsWith(cmd.command)).
                findAny().orElse(OTHER);//.orElseThrow(() -> new RuntimeException("Несуществующая команда " + text));
    }

    public static String getCommandPrefix() {
        return "/";
    }

    public String getCommand() {
        return command;
    }
}
