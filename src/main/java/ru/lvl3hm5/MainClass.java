package ru.lvl3hm5;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class MainClass {
    public static final int CARS_COUNT = 4;

    public static void main(String[] args) throws BrokenBarrierException, InterruptedException {
        System.out.println("ВАЖНОЕ ОБЪЯВЛЕНИЕ >>> Подготовка!!!");
        Race race = new Race(new Road(60), new Tunnel(), new Road(40));
        Car[] cars = new Car[CARS_COUNT];
        CyclicBarrier cyclicBarrier = new CyclicBarrier(CARS_COUNT);
        final CountDownLatch cdl = new CountDownLatch(CARS_COUNT);
        final CountDownLatch cdl1 = new CountDownLatch(CARS_COUNT);
        final CountDownLatch cdl2 = new CountDownLatch(1);
        Semaphore smp = new Semaphore(2);
        AtomicBoolean isWinner = new AtomicBoolean();
        isWinner.set(false);
        for (int i = 0; i < cars.length; i++) {
            cars[i] = new Car(race, 20 + (int) (Math.random() * 10), cyclicBarrier, cdl, smp, cdl1, isWinner, cdl2);
        }

        for (int i = 0; i < cars.length; i++) {
            new Thread(cars[i]).start();
        }
        cdl.await(); //ждем когда все машины будут готовы
        System.out.println("ВАЖНОЕ ОБЪЯВЛЕНИЕ >>> Гонка началась!!!");
        cdl2.countDown(); //остановим не основные потоки, пока не напечатаем объявление
        cdl1.await(); //ждем пока все машины финишируют
        System.out.println("ВАЖНОЕ ОБЪЯВЛЕНИЕ >>> Гонка закончилась!!!");
    }
}







