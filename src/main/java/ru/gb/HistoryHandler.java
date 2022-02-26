package ru.gb;

import javafx.application.Platform;

import java.io.*;

public class HistoryHandler {
    FileWriter fileWriter;
    File file;
    BufferedReader bufferedReader;
    private final String login;

    public HistoryHandler(String login) {
        this.login = login;
    }

    public void writeInFile(String message) {
        try {
            fileWriter.write(message + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void readFile(NetChatController controller) {
        try {
            bufferedReader = new BufferedReader(new FileReader(file));
            LineNumberReader count = new LineNumberReader(new FileReader(file));
            String line;
            try {
                count.skip(Long.MAX_VALUE);
                int result = count.getLineNumber() + 1;
                int strCount = 100;
                if (result > strCount) {
                    result -= strCount;
                    for (int i = 1; i < result; ++i)
                        bufferedReader.readLine();
                }
                while ((line = bufferedReader.readLine()) != null) {
                    String finalLine = line;
                    Platform.runLater(() -> controller.getTipArea().appendText(finalLine + "\n"));
                }
                count.close();
                bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    public void useFile() {
        String path = new File("").getAbsolutePath();
        String newDir = "\\history";
        boolean mkdir = new File(path + newDir).mkdir();
        file = new File(path + newDir + File.separator + "history_" + login + ".txt");
        if (!file.exists()) {
            try {
                final boolean newFile = file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            //System.out.println(file.getAbsolutePath());
            //System.out.println(file.getName());
            fileWriter = new FileWriter(file, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void closeWriter() {
        try {
            if (fileWriter != null) {
                fileWriter.flush();
                fileWriter.close();
                fileWriter = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
