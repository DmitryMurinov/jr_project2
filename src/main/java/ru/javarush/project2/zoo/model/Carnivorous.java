package ru.javarush.project2.zoo.model;

import ru.javarush.project2.zoo.util.ZooProperties;

import java.util.*;
import java.util.stream.Collectors;

public class Carnivorous extends Animal {

    public Carnivorous(ItemProperties value) {
        super(value);
    }

    /**
     * Can be replaced with app property later (in that case static should be removed)
     * Determinate to move towards grass or from predators, based on fullness of creature.
     * Number recommendation: something 0.1 to 1.0.
     * Used for very basic feed or flee strategy.
     */
    private static final double LINE_BETWEEN_STARVATION_AND_PREDATORS_RISKS = 0.8;

    /**
     * Carnivorous lost food implementation
     */
    @Override
    public void lostFood(ZooProperties zooProperties) {
        if(zooProperties.getItemsProps().get(getTypeName()).getMaxFood() == 0.0){
            return;
        }

        double maxFoodForType = zooProperties.getItemsProps().get(this.getTypeName()).getMaxFood();
        int worldStarveTick = Integer.parseInt(zooProperties.getProperty("creature.ticks.to.starve"));

        double lostFood = maxFoodForType / worldStarveTick;

        setCurrentFood(getCurrentFood() - lostFood);

        if (getCurrentFood() <= 0) {
            die();
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
        for (Map.Entry<String, Integer> prey : canEat.entrySet()) {
            if (prey.getValue() >= chanceToEat) {
                haveChance.add(zooProperties.getItemsProps().get(prey.getKey()));
            }
        }

        if (haveChance.size() == 0) {
            setEatAlready(true);
            return;
        }

        haveChance.sort(Comparator.comparing(ItemProperties::getWeight));

        for (int i = haveChance.size() - 1; i >= 0; i--) {
            List<TileItem> preyList =
                    land.getIsland()[getPosY()][getPosX()].getTileItems().get(haveChance.get(i).getTypeName())
                            .stream()
                            .filter(TileItem::isAlive)
                            .collect(Collectors.toList());

            if (preyList.size() == 0) {
                continue;
            }

            for (int j = 0; j < preyList.size(); j++) {
                synchronized (preyList.get(j)) {
                    TileItem victim = preyList.get(j);

                    //Если жертва умерла до её блокировки, переходим к следующей этого же типа
                    if(!victim.isAlive()){
                        continue;
                    }

                    double newFood = Math.min(victim.getWeight() + this.getCurrentFood(), this.getMaxFood());

                    this.setCurrentFood(newFood);
                    victim.die();
                    setEatAlready(true);
                    return;
                }
            }
        }
    }

    @Override
    public synchronized void move(Land land, ZooProperties zooProperties) {
        List<AnimalView> posibilities = getSurroundings().getAsList().stream()
                .filter(s -> land.getIsland()[s.getPosY()][s.getPosX()].getTileItems().get(getTypeName()).size() < zooProperties.getItemsProps().get(getTypeName()).getMaxOnTileCount())
                .collect(Collectors.toList());

        if(posibilities.size() == 0){
            return;
        }

        while(posibilities.size() > 0) {
            if (getCurrentFood() < getMaxFood() * LINE_BETWEEN_STARVATION_AND_PREDATORS_RISKS) {
                AnimalView tileToMove = posibilities.stream()
                        .max(Comparator.comparing(t -> {
                            Map<String, Integer> preyTypes = zooProperties.getEatChain().get(getTypeName());
                            long foodLevel = 0L;

                            for(Map.Entry<String, Integer> preyType : preyTypes.entrySet()){
                                foodLevel += (long) t.getLocalCount().get(preyType.getKey()) * preyType.getValue()
                                        * zooProperties.getItemsProps().get(preyType.getKey()).getWeight();
                            }

                            return foodLevel;
                        }))
                        .get();

                boolean moveResult = moveToTitle(land, zooProperties, posibilities, tileToMove);
                if(moveResult){
                    return;
                }

            } else {
                AnimalView tileToMove = posibilities.stream()
                        .min(Comparator.comparing(t -> {
                            Map<String, Integer> predators = zooProperties.getEatenChain().get(getTypeName());
                            if(predators == null){
                                return 0L;
                            }
                            long dangerLevel = 0L;

                            for(Map.Entry<String, Integer> predator : predators.entrySet()){
                                dangerLevel += (long) t.getLocalCount().get(predator.getKey()) * predator.getValue();
                            }

                            return dangerLevel;
                        }))
                        .get();

                boolean moveResult = moveToTitle(land, zooProperties, posibilities, tileToMove);
                if(moveResult){
                    return;
                }
            }
        }
    }

}
