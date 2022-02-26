package ru.hm4;

import static ru.hm4.Letters.A;

public class Homework4 {
    public static volatile String currentLetter = A.getLetter();
    public static void main(String[] args) {
        final Object mon = new Object();
        Thread t1 = new MyThread(mon,"A");
        Thread t2 = new MyThread(mon,"B");
        Thread t3 = new MyThread(mon,"C");
        t1.start();
        t2.start();
        t3.start();
    }
}
