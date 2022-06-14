package ru.javarush.project2.zoo;


import ru.javarush.project2.zoo.utils.Populate;
import ru.javarush.project2.zoo.utils.ZooProperties;

public class Main {


    public static void main(String[] args) {

        ZooProperties properties = ZooProperties.getInstance();

        new Populate().fillLandWithLife();

        System.out.println("main print");

    }

}
