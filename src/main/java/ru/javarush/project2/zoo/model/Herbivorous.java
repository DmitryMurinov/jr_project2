package ru.javarush.project2.zoo.model;

import ru.javarush.project2.zoo.utils.ZooProperties;

import java.util.List;
import java.util.stream.Collectors;

public class Herbivorous extends Animal {

    public Herbivorous(ItemProperties value) {
        super(value);
    }

    private final int MAX_TICKS_BETWEEN_FEEDING_NO_STARVE = 5;

    /**
     * Herbivorous lost weight implementation
     * A bit complex calculations need to avoid of death for high max food creature & low plant weight combination issues
     */
    public synchronized void lostWeight(Land land, ZooProperties zooProperties){
        double plantWeight = zooProperties.getItemsProps().get(ZooProperties.VEGETATION_TYPE_NAME).getWeight();


        double maxFoodForType = zooProperties.getItemsProps().get(this.getTypeName()).getMaxFood();
        int worldStarveTick = Integer.parseInt(zooProperties.getProperty("creature.ticks.to.starve"));

        double lostWeight = Math.min(plantWeight / MAX_TICKS_BETWEEN_FEEDING_NO_STARVE, maxFoodForType / worldStarveTick);

        setWeight(getWeight() - lostWeight);
        if(getWeight() <= 0){
            die(land);
        }
    }


    /**
     * Herbivorous eat implementation
     */
    @Override
    public void eat(Land land, ZooProperties zooProperties) {
        if (isEatAlready()) {
            return;
        }

        synchronized (land.getIsland()[getPosY()][getPosX()].getTileItems().get(ZooProperties.VEGETATION_TYPE_NAME)) {
            List<TileItem> food = land.getIsland()[getPosY()][getPosX()].getTileItems().get(ZooProperties.VEGETATION_TYPE_NAME);

            if (food.size() == 0) {
                return;
            }

            List<TileItem> alive = food.stream().filter(f -> f.isAlive()).collect(Collectors.toList());

            for (TileItem foodItem : alive) {
                if (getCurrentFood() + foodItem.getWeight() < getMaxFood()) {
                    setCurrentFood(getCurrentFood() + food.get(0).getWeight());
                } else {
                    setCurrentFood(getMaxFood());
                }

                food.get(0).die(land);
                setEatAlready(true);
                return;
            }
        }
    }



}
