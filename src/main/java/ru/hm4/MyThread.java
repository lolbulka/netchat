package ru.hm4;

import static ru.hm4.Homework4.currentLetter;
import static ru.hm4.Letters.*;

public class MyThread extends Thread {
    private final String letter;
    private final Object monitor;

    public MyThread(Object monitor, String letter) {
        this.letter = letter;
        this.monitor = monitor;
    }

    @Override
    public void run() {
        printLetter();
    }

    private void printLetter() {
        synchronized (monitor) {
            try {
                for (int i = 0; i < 5; i++) {
                    while (!currentLetter.equals(A.getLetter()) && letter.equals(A.getLetter()) ||
                            !currentLetter.equals(B.getLetter()) && letter.equals(B.getLetter()) ||
                            !currentLetter.equals(C.getLetter()) && letter.equals(C.getLetter())) {
                        monitor.wait();
                    }
                    System.out.print(letter);
                    if (letter.equals(A.getLetter())) {
                        currentLetter = B.getLetter();
                    }
                    if (letter.equals(B.getLetter())) {
                        currentLetter = C.getLetter();
                    }
                    if (letter.equals(C.getLetter())) {
                        currentLetter = A.getLetter();
                    }
                    monitor.notifyAll();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}
