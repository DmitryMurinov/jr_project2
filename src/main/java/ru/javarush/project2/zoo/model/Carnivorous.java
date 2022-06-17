package ru.javarush.project2.zoo.model;

import ru.javarush.project2.zoo.utils.ZooProperties;

import java.util.*;

public class Carnivorous extends Animal{

    public Carnivorous(ItemProperties value) {
        super(value);
    }

    /**
     * Carnivorous lost weight implementation
     */
    public void lostWeight(Land land, ZooProperties zooProperties){
        double maxFoodForType = zooProperties.getItemsProps().get(this.getTypeName()).getMaxFood();
        int worldStarveTick = Integer.parseInt(zooProperties.getProperty("creature.ticks.to.starve"));

        double lostWeight = maxFoodForType / worldStarveTick;

        setWeight(getWeight() - lostWeight);
        if(getWeight() <= 0){
            die(land);
        }
    }

    /**
     * Carnivorous eat implementation
     */
    @Override
    public synchronized void eat(Land land, ZooProperties zooProperties) {
        if (isEatAlready()) {
            return;
        }

        int chanceToEat = java.util.concurrent.ThreadLocalRandom.current().nextInt(0, 101);
        List<ItemProperties> haveChance = new ArrayList<>();

        Map<String, Integer> canEat = zooProperties.getEatChain().get(this.getTypeName());
        for(Map.Entry<String, Integer> prey : canEat.entrySet()){
            if(prey.getValue() >= chanceToEat){
                haveChance.add(zooProperties.getItemsProps().get(prey.getKey()));
            }
        }

        if(haveChance.size() == 0){
            setEatAlready(true);
            return;
        }

        haveChance.sort(Comparator.comparing(ItemProperties::getWeight));

        //need to think about deadlocks here
        for (int i = haveChance.size() - 1; i >= 0; i--) {
            synchronized (land.getIsland()[getPosY()][getPosX()].getTileItems().get(haveChance.get(i).getTypeName())){
                List<TileItem> preyList = land.getIsland()[getPosY()][getPosX()].getTileItems().get(haveChance.get(i).getTypeName());
                if(preyList.size() == 0){
                    continue;
                }

                synchronized (preyList.get(0)) {
                    TileItem victim = preyList.get(0);
                    double newFood = Math.min(victim.getWeight() + this.getCurrentFood(), this.getMaxFood());

                    this.setCurrentFood(newFood);
                    victim.die(land);
                    setEatAlready(true);
                    return;
                }
            }
        }
    }


}
