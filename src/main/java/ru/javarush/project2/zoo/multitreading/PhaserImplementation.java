package ru.javarush.project2.zoo.multitreading;

import ru.javarush.project2.zoo.model.Land;
import ru.javarush.project2.zoo.service.*;
import ru.javarush.project2.zoo.util.ZooProperties;

import java.util.concurrent.Phaser;

public class PhaserImplementation implements Runnable {

    private Phaser phaser;

    private AnimateLand animateLand;
    private Land land;
    private ZooProperties zooProperties;

    private int zooTtl;

    public PhaserImplementation(Land land, ZooProperties zooProperties, AnimateLand animateLand, Phaser phaser) {
        this.animateLand = animateLand;
        this.land = land;
        this.zooProperties = zooProperties;
        this.phaser = phaser;

        phaser.register();
        zooTtl = Integer.parseInt(zooProperties.getProperty("zoo.ttl"));
    }

    @Override
    public void run() {
        while (zooTtl > 0) {

            if (animateLand instanceof Statistics) {
                Statistics statistics = (Statistics) animateLand;
                statistics.setTickNo(phaser.getPhase());
            }

            animateLand.perform(land, zooProperties);


//            if(animateLand instanceof Animator){
//                System.out.println("Animator step: " + phaser.getPhase());
//            }

//            if(animateLand instanceof Gardener){
//                System.out.println("Gardener step: " + phaser.getPhase());
//            }

//            if(animateLand instanceof Hearse){
//                System.out.println("Hearse step: " + phaser.getPhase());
//            }

//            if(animateLand instanceof Timer){
//                System.out.println("Timer step: " + phaser.getPhase());
//            }

            phaser.arriveAndAwaitAdvance();
            zooTtl--;
        }

        phaser.arriveAndDeregister();
    }

}
