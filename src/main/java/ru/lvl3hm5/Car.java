package ru.lvl3hm5;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

public class Car implements Runnable {
    private static int CARS_COUNT;
    private Race race;
    private int speed;
    private String name;
    private CyclicBarrier cyclicBarrier;
    private CountDownLatch cdl;
    private CountDownLatch cdl1;
    private CountDownLatch cdl2;
    private  Semaphore smp;
    private  AtomicBoolean isWinner;
    public String getName() {
        return name;
    }
    public int getSpeed() {
        return speed;
    }
    public Car(Race race, int speed, CyclicBarrier cyclicBarrier, CountDownLatch cdl,
               Semaphore smp, CountDownLatch cdl1, AtomicBoolean isWinner, CountDownLatch cdl2) {
        this.race = race;
        this.speed = speed;
        CARS_COUNT++;
        this.cyclicBarrier = cyclicBarrier; //для сбора по подготовке и готовности
        this.cdl = cdl;
        this.cdl1 = cdl1;
        this.cdl2 = cdl2;
        this.name = "Участник #" + CARS_COUNT;
        this.smp = smp; //для ограничения кол-ва машин в тоннеле = 2
        this.isWinner = isWinner; //для определения победителя
    }
    @Override
    public void run() {
        try {
            System.out.println(this.name + " готовится");
            cyclicBarrier.await();
            Thread.sleep(500 + (int)(Math.random() * 800));
            System.out.println(this.name + " готов");
            cyclicBarrier.await();
            cdl.countDown(); //основной поток ждет вывода надписи на экран о начале гонки, пока все не будут готовы
            cdl2.await();
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (int i = 0; i < race.getStages().size(); i++) {
            race.getStages().get(i).go(this, smp);
        }
        if (!isWinner.get()) { //кто первый проедет все уровни, тот и победитель
            System.out.println(this.name + " - WIN");
            isWinner.set(true);
        }
        cdl1.countDown();
    }
}
