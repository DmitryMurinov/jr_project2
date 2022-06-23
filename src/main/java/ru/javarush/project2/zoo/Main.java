package ru.javarush.project2.zoo;


import ru.javarush.project2.zoo.model.Land;
import ru.javarush.project2.zoo.multitreading.PhasersHandler;
import ru.javarush.project2.zoo.service.*;
import ru.javarush.project2.zoo.util.Populate;
import ru.javarush.project2.zoo.util.ZooProperties;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class Main {


    public static void main(String[] args) throws UnsupportedEncodingException {

        ZooProperties zooProperties = new ZooProperties();
        Land land = new Land();

        new Populate(land, zooProperties).fillLandWithLife();

        AnimateLand hearse = new Hearse();
        AnimateLand gardener = new Gardener();
        AnimateLand animator = new Animator();
        AnimateLand timer = new Timer();
        AnimateLand statistics = new Statistics();

        List<AnimateLand> services = new ArrayList<>();
        services.add(timer);
        services.add(hearse);
        services.add(gardener);
        services.add(animator);
        services.add(statistics);

        PhasersHandler handler = new PhasersHandler();
        handler.handle(land, zooProperties, services);
    }

}
