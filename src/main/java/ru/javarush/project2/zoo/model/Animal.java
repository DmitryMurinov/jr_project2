package ru.javarush.project2.zoo.model;

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

    public Animal(ItemProperties value) {
        this.maxTickMove = value.getMaxTickMove();
        this.maxFood = value.getMaxFood();
    }

    

}
