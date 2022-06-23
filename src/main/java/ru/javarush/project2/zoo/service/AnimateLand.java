package ru.javarush.project2.zoo.service;

import ru.javarush.project2.zoo.model.Land;
import ru.javarush.project2.zoo.util.ZooProperties;

public interface AnimateLand {

   void perform(Land land, ZooProperties zooProperties);

}
