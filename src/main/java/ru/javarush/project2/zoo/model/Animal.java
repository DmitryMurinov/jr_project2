package ru.javarush.project2.zoo.model;

import lombok.Data;
import ru.javarush.project2.zoo.utils.ZooProperties;

import java.util.Properties;

@Data
public class Animal extends TileItem {

    /**
     * For how many tiles creature can move in 1 tick
     */
    private int maxTickMove;

    /**
     * Max food for this type (=max HP in games).
     */
    private double maxFood;

    /**
     * Current food for this type (=current HP in games). When currentFood reaches 0 creature will starve to death.
     */
    private double currentFood;

    /**
     * Actions status for current tick
     */
    private boolean eatAlready;

    public Animal(ItemProperties value) {
        this.maxTickMove = value.getMaxTickMove();
        this.maxFood = value.getMaxFood();
    }

    /**
     * liveCircle of animal for 1 tick
     */
    @Override
    public void liveTick(Land land, ZooProperties zooProperties){
        lostWeight(land, zooProperties);
        eat(land, zooProperties);
    }

    public void eat(Land land, ZooProperties zooProperties){}

    public void lostWeight(Land land, ZooProperties zooProperties){}
}
