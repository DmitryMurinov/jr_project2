package ru.javarush.project2.zoo.service;

import ru.javarush.project2.zoo.model.Land;
import ru.javarush.project2.zoo.util.ZooProperties;

import java.util.concurrent.TimeUnit;

public class Timer implements AnimateLand {

    private final int MIN_TICK_TIME_MILLIS = 250;

    @Override
    public void perform(Land land, ZooProperties zooProperties){
        try {
            TimeUnit.MILLISECONDS.sleep(MIN_TICK_TIME_MILLIS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}
