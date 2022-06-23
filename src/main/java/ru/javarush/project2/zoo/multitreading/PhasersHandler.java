package ru.javarush.project2.zoo.multitreading;

import ru.javarush.project2.zoo.model.Land;
import ru.javarush.project2.zoo.service.AnimateLand;
import ru.javarush.project2.zoo.util.ZooProperties;

import java.util.List;
import java.util.concurrent.Phaser;
import java.util.stream.Collectors;

public class PhasersHandler {

    public void handle(Land land, ZooProperties zooProperties, List<AnimateLand> services){

        Phaser phaser = new Phaser();
        phaser.register();

        List<PhaserImplementation> workers = services.stream()
                .map(s -> new PhaserImplementation(land, zooProperties, s, phaser))
                .collect(Collectors.toList());

        int zooTtl = Integer.parseInt(zooProperties.getProperty("zoo.ttl"));

        for (PhaserImplementation worker : workers) {
            new Thread(worker).start();
        }

        while(zooTtl > 0) {
            phaser.arriveAndAwaitAdvance();
            zooTtl--;

            if(zooTtl % 10 == 0){
                System.out.println("123");
            }
        }

        phaser.arriveAndDeregister();
    }

}
