package ru.javarush.project2.zoo.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.javarush.project2.zoo.util.ZooProperties;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

@Data
@EqualsAndHashCode(callSuper = true)
public class Animal extends TileItem {

    /**
     * For how many tiles creature can move in 1 tick
     */
    private int maxTickMoves;

    /**
     * For how many tiles left for creature to move in current tick
     */
    private int leftTickMoves;

    /**
     * Max food for this type (=max HP in games).
     */
    private double maxFood;

    /**
     * Current food for this type (=current HP in games). When currentFood reaches 0 creature will starve to death.
     */
    private double currentFood;

    /**
     * Action status for current tick
     */
    private boolean eatAlready;

    /**
     * Action status for current tick
     */
    private boolean breedAlready;

    /**
     * Need for breed behavior, to avoid deadlocks
     */
    private final ReentrantLock lock = new ReentrantLock(true);

    /**
     * Need to decide where to move
     */
    private AnimalView surroundings;

    public Animal(ItemProperties value) {
        super(value);
        this.maxTickMoves = value.getMaxTickMove();
        this.maxFood = value.getMaxFood();
        this.currentFood = value.getMaxFood();
    }

    /**
     * liveCircle of animal for 1 tick
     */
    @Override
    public void liveTick(Land land, ZooProperties zooProperties) {
        if(!isAlive()){
            return;
        }

        lostFood(zooProperties);
        leftTickMoves = maxTickMoves;

        while (isAlive() && leftTickMoves > 0) {
            eat(land, zooProperties);
            breed(land, zooProperties);
            lookAround(land, zooProperties);
            move(land, zooProperties);
            leftTickMoves--;
        }
    }


    public synchronized void move(Land land, ZooProperties zooProperties) {
    }

    private void lookAround(Land land, ZooProperties zooProperties) {

        //Add local
        {
            surroundings = new AnimalView(land, zooProperties, getPosY(), getPosX());
        }

        //Add north
        if (getPosY() > 0) {
            AnimalView northSurroundings = new AnimalView(land, zooProperties, getPosY() - 1, getPosX());

            surroundings.setNorth(northSurroundings);
        }


        //Add east
        int landSizeX = Integer.parseInt(zooProperties.getProperty("land.size.x"));
        if (getPosX() + 2 < landSizeX) {
            AnimalView eastSurroundings = new AnimalView(land, zooProperties,  getPosY(), getPosX() + 1);

            surroundings.setEast(eastSurroundings);
        }


        //Add south
        int landSizeY = Integer.parseInt(zooProperties.getProperty("land.size.y"));
        if (getPosY() + 2 < landSizeY) {
            AnimalView southSurroundings = new AnimalView(land, zooProperties, getPosY() + 1, getPosX());

            surroundings.setSouth(southSurroundings);
        }


        //Add west
        if (getPosX() > 0) {
            AnimalView westSurroundings = new AnimalView(land, zooProperties, getPosY(), getPosX() - 1);

            surroundings.setWest(westSurroundings);
        }
    }

    private void breed(Land land, ZooProperties zooProperties) {

        if (land.getIsland()[getPosY()][getPosX()].getTileItems().get(getTypeName()).size()
                >= zooProperties.getItemsProps().get(getTypeName()).getMaxOnTileCount()) {
            return;
        }

        List<Animal> mates = land.getIsland()[getPosY()][getPosX()].getTileItems().get(getTypeName())
                .stream()
                .map(i -> (Animal) i)
                .filter(i -> i.isAlive() && !i.isBreedAlready())
                .collect(Collectors.toList());
        mates.remove(this);

        if (mates.size() == 0) {
            return;
        }


        for (int i = 0; i < mates.size(); i++) {
            synchronized (mates.get(i)) {
                Animal mate = mates.get(i);

                if (!mate.isAlive()) {
                    continue;
                }

                if (mate.isBreedAlready()) {
                    continue;
                }

                if (lock.tryLock()) {
                    try {
                        if (!this.isAlive() || this.isBreedAlready()) {
                            return;
                        }

                        this.setBreedAlready(true);
                        mate.setBreedAlready(true);

                        Animal animal = (Animal) getTileItemFactory().make(this, zooProperties);
                        animal.setPosY(this.getPosY());
                        animal.setPosX(this.getPosX());

                        synchronized (land.getIsland()[getPosY()][getPosX()].getTileItems().get(getTypeName())) {
                            synchronized (land.getIsland()[getPosY()][getPosX()].getTileHistory()){
                                String action = "Два небинарных " + getTypeName() + " размножились";

                                if(land.getIsland()[getPosY()][getPosX()].getTileHistory().containsKey(action)){
                                   long totalAction = land.getIsland()[getPosY()][getPosX()].getTileHistory().get(action);
                                   totalAction++;
                                    land.getIsland()[getPosY()][getPosX()].getTileHistory().put(action, totalAction);
                                } else {
                                    land.getIsland()[getPosY()][getPosX()].getTileHistory().put(action, 1L);
                                }
                            }

                            land.getIsland()[getPosY()][getPosX()].getTileItems().get(animal.getTypeName()).add(animal);
                        }
                    } finally {
                        lock.unlock();
                    }
                    return;
                }
            }
        }
    }

    public void eat(Land land, ZooProperties zooProperties) {
    }

    public void lostFood(ZooProperties zooProperties) {
    }

    synchronized boolean moveToTitle(Land land, ZooProperties zooProperties, List<AnimalView> posibilities, AnimalView tileToMove) {

        if (land.getIsland()[tileToMove.getPosY()][tileToMove.getPosX()].getTileItems().get(getTypeName()).contains(this)) {
            return true;
        }


        synchronized (land.getIsland()[tileToMove.getPosY()][tileToMove.getPosX()].getTileItems().get(getTypeName())) {
            int actual = land.getIsland()[tileToMove.getPosY()][tileToMove.getPosX()].getTileItems().get(getTypeName()).size();
            int max = zooProperties.getItemsProps().get(getTypeName()).getMaxOnTileCount();

            if (actual >= max) {
                posibilities.remove(tileToMove);
                return false;
            }

            land.getIsland()[getPosY()][getPosX()].getTileItems().get(getTypeName()).remove(this);
            land.getIsland()[tileToMove.getPosY()][tileToMove.getPosX()].getTileItems().get(getTypeName()).add(this);
            return true;
        }
    }
}
