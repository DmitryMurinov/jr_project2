package ru.javarush.project2.zoo;


import ru.javarush.project2.zoo.model.Land;
import ru.javarush.project2.zoo.utils.Populate;
import ru.javarush.project2.zoo.utils.ZooProperties;

public class Main {


    public static void main(String[] args) {

        ZooProperties properties = new ZooProperties();
        Land land = new Land();

        new Populate(land, properties).fillLandWithLife();

        System.out.println("main print");

    }

}
